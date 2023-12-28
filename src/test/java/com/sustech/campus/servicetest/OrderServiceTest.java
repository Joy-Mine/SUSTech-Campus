package com.sustech.campus.servicetest;

import com.sustech.campus.entity.*;
import com.sustech.campus.entity.Order;
import com.sustech.campus.enums.OrderStatus;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.service.OrderService;
import com.sustech.campus.service.StoreService;
import com.sustech.campus.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderServiceTest {
    private UserService userService;

    private StoreService storeService;

    private OrderService orderService;

    private List<User> userList;

    private List<Store> storeList;

    private List<Goods> goodsList;

    private List<Order> orderList;

    private List<OrderItem> orderItemList;

    private Map<Long, Integer> quantityMap;

    @Autowired
    public OrderServiceTest(UserService userService, StoreService storeService, OrderService orderService) {
        this.userService = userService;
        this.storeService = storeService;
        this.orderService = orderService;
        this.userList = new ArrayList<>();
        this.storeList = new ArrayList<>();
        this.goodsList = new ArrayList<>();
        this.orderList = new ArrayList<>();
        this.orderItemList = new ArrayList<>();
        this.quantityMap = new HashMap<>();
    }

    @BeforeEach
    void insert() {
        int numUsers = 3;
        int numStores = 3;
        int numGoods = 10;
        int numOrders = 10;
        int numOrderItems = 20;
        Random random = new Random();
        int[] availableQuantities = new int[numGoods];

        for (int i = 0; i < numUsers; ++i) {
            User user = new User();
            user.setName("user_" + i);
            user.setPassword("pass_" + i);
            user.setType(UserType.USER);
            User result = userService.registerUser(
                user.getName(),
                user.getPassword(),
                user.getType()
            );
            user.setToken(result.getToken());
            user.setId(result.getId());
            this.userList.add(user);
        }
        for (int i = 0; i < numStores; ++i) {
            Store store = new Store();
            store.setName("store_" + i);
            Long storeId = storeService.addStore(store.getName());
            store.setId(storeId);
            this.storeList.add(store);
        }
        for (int i = 0; i < numGoods; ++i) {
            Goods goods = new Goods();
            goods.setName("goods_" + i);
            int localStoreId = random.nextInt(numStores);
            goods.setStoreId(this.storeList.get(localStoreId).getId());
            goods.setPrice(BigDecimal.valueOf(random.nextLong(1000)));
            goods.setQuantity(random.nextInt(100, 1000));
            Long goodsId = storeService.addGoods(
                    goods.getStoreId(),
                    goods.getName(),
                    goods.getPrice(),
                    goods.getQuantity()
            );
            goods.setId(goodsId);
            goods.setHidden(false);
            this.goodsList.add(goods);
            availableQuantities[i] = goods.getQuantity();
        }
        for (int i = 0; i < numOrders; ++i) {
            Order order = new Order();
            int localUserId = random.nextInt(numUsers);
            order.setPurchaser(this.userList.get(localUserId).getId());
            order.setItems(new ArrayList<>());
            order.setStatus(OrderStatus.WAITING_PAYMENT);
            this.orderList.add(order);
        }
        for (int i = 0; i < numOrderItems; ++i) {
            OrderItem item = new OrderItem();
            int localOrderId;
            if (i < numOrders) {
                localOrderId = i;
            } else {
                localOrderId = random.nextInt(numOrders);
            }
            int localGoodsId = random.nextInt(numGoods);
            if (availableQuantities[localGoodsId] == 0
                || this.orderList.get(localOrderId).getItems().stream().map(OrderItem::getGoodsId)
                    .anyMatch(e -> e.equals(this.goodsList.get(localGoodsId).getId()))) {
                continue;
            }
            this.orderList.get(localOrderId).getItems().add(item);
            item.setGoodsId(this.goodsList.get(localGoodsId).getId());
            int amount = random.nextInt(1, availableQuantities[localGoodsId] + 1);
            item.setAmount(amount);
            availableQuantities[localGoodsId] -= amount;
            item.setAmount(amount);
            item.setPrice(this.goodsList.get(localGoodsId).getPrice());
            this.orderItemList.add(item);
        }
        for (int i = 0; i < numGoods; ++i) {
            quantityMap.put(this.goodsList.get(i).getId(), (int) this.goodsList.get(i).getQuantity());
        }
        for (int i = 0; i < numOrders; ++i) {
            Order order = this.orderList.get(i);
            for (OrderItem item : order.getItems()) {
                quantityMap.replace(
                        item.getGoodsId(),
                        quantityMap.get(item.getGoodsId()) - item.getAmount()
                );
            }
            Order result = orderService.createOrder(
                    order.getPurchaser(),
                    order.getItems().stream().map(OrderItem::getGoodsId).toList(),
                    order.getItems().stream().map(OrderItem::getAmount).toList()
            );
            assertNotNull(result.getId());
            assertNotNull(result.getTime());
            assertTrue(Math.abs(result.getTime() - System.currentTimeMillis()) < 1000);
            order.setId(result.getId());
            order.setTime(result.getTime());
            for (OrderItem item : order.getItems()) {
                item.setOrderId(result.getId());
            }
        }
    }

    @AfterEach
    void clean() {
        for (Order order : this.orderList) {
            assertTrue(orderService.deleteOrder(order.getId()));
        }
        for (Goods goods : this.goodsList) {
            storeService.deleteGoods(goods.getId());
        }
        for (Store store : this.storeList) {
            storeService.deleteStore(store.getId());
        }
        for (User user : this.userList) {
            userService.deleteUser(user.getId());
        }
    }

    void testQuantityEquals() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {

        }
        for (Goods goods : this.goodsList) {
            assertEquals(
                    this.storeService.getGoodsById(goods.getId()).getQuantity(),
                    quantityMap.get(goods.getId())
            );
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testListAllOrders() {
        Random random = new Random();
        int numTimes = 5;
        List<Long> userIds = new ArrayList<>();
        userIds.add(null);
        userIds.addAll(this.userList.stream().map(User::getId).toList());
        List<Long> times = new ArrayList<>();
        times.add(null);
        times.addAll(Stream.generate(random::nextLong).limit(numTimes).sorted().toList());
        times.add(null);
        for (Long userId : userIds) {
            for (Long start : times) {
                for (Long end : times) {
                    assertIterableEquals(
                        orderService.listAllOrders(userId, start, end).stream()
                                .sorted(Comparator.comparing(Order::getId)).toList(),
                        this.orderList.stream()
                                .filter(e -> userId == null || e.getPurchaser().equals(userId))
                                .filter(e -> start == null || e.getTime().compareTo(start) >= 0)
                                .filter(e -> end == null || e.getTime().compareTo(end) <= 0)
                                .map(e -> {
                                    Order order = new Order();
                                    order.setId(e.getId());
                                    order.setStatus(e.getStatus());
                                    order.setTime(e.getTime());
                                    order.setPurchaser(e.getPurchaser());
                                    return order;
                                })
                                .sorted(Comparator.comparing(Order::getId))
                                .toList()
                    );
                }
            }
        }
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testGetOrderById() {
        for (Order order : this.orderList) {
            Order expected = new Order();
            expected.setId(order.getId());
            expected.setStatus(order.getStatus());
            expected.setTime(order.getTime());
            expected.setPurchaser(order.getPurchaser());
            assertEquals(
                    orderService.getOrderById(order.getId()),
                    expected
            );
        }
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testOrderExists() {
        for (Order order : this.orderList) {
            assertTrue(orderService.orderExists(order.getId()));
        }
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testGetFullOrderById() {
        for (Order order : this.orderList) {
            assertEquals(
                    orderService.getFullOrderById(order.getId()),
                    order
            );
        }
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testOrderPaid() {
        for (Order order : this.orderList) {
            assertEquals(
                    orderService.getOrderById(order.getId()).getStatus(),
                    OrderStatus.WAITING_PAYMENT
            );
            assertTrue(orderService.orderPaid(order.getId()));
            assertEquals(
                    orderService.getOrderById(order.getId()).getStatus(),
                    OrderStatus.PAID
            );
        }
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testCreateOrder() {
        testQuantityEquals();
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testOrderCancelled() {
        testQuantityEquals();
        for (Order order : this.orderList) {
            for (OrderItem item : order.getItems()) {
                quantityMap.replace(
                        item.getGoodsId(),
                        quantityMap.get(item.getGoodsId()) + item.getAmount()
                );
            }
            assertEquals(
                    orderService.getOrderById(order.getId()).getStatus(),
                    OrderStatus.WAITING_PAYMENT
            );
            assertTrue(orderService.orderCancelled(order.getId()));
            assertEquals(
                    orderService.getOrderById(order.getId()).getStatus(),
                    OrderStatus.CANCELLED
            );
        }
        testQuantityEquals();
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testOrderFinished() {
        for (Order order : this.orderList) {
            orderService.orderPaid(order.getId());
            assertEquals(
                    orderService.getOrderById(order.getId()).getStatus(),
                    OrderStatus.PAID
            );
            assertTrue(orderService.orderFinished(order.getId()));
            assertEquals(
                    orderService.getOrderById(order.getId()).getStatus(),
                    OrderStatus.FINISHED
            );
        }
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testOrderRefunded() {
        testQuantityEquals();
        for (Order order : this.orderList) {
            orderService.orderPaid(order.getId());
            for (OrderItem item : order.getItems()) {
                quantityMap.replace(
                        item.getGoodsId(),
                        quantityMap.get(item.getGoodsId()) + item.getAmount()
                );
            }
            assertEquals(
                    orderService.getOrderById(order.getId()).getStatus(),
                    OrderStatus.PAID
            );
            assertTrue(orderService.orderRefunded(order.getId()));
            assertEquals(
                    orderService.getOrderById(order.getId()).getStatus(),
                    OrderStatus.REFUNDED
            );
        }
        testQuantityEquals();
    }
}
