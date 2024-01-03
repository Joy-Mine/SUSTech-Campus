package com.sustech.campus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sustech.campus.entity.Goods;
import com.sustech.campus.entity.Order;
import com.sustech.campus.entity.OrderItem;
import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.OrderService;
import com.sustech.campus.service.StoreService;
import com.sustech.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/order")
public class OrderController {

    private final UserService userService;

    private final StoreService storeService;

    private final OrderService orderService;

    @Autowired
    public OrderController(UserService userService, StoreService storeService, OrderService orderService) {
        this.userService = userService;
        this.storeService = storeService;
        this.orderService = orderService;
    }

    @Access(level = UserType.USER)
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(HttpServletRequest request, @RequestBody List<OrderItem> items) {
        String token = request.getHeader("TOKEN");
        if (token == null) {
            return ResponseEntity.badRequest().body("Creation failed: Illegal user.");
        }
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Creation failed: Illegal user.");
        }
        Order order = orderService.createOrder(
                user.getId(),
                items.stream().map(OrderItem::getGoodsId).toList(),
                items.stream().map(OrderItem::getAmount).toList()
        );
        if (order != null) {
            return ResponseEntity.ok("Creation succeed.");
        } else {
            return ResponseEntity.badRequest().body("Creation failed: Illegal items.");
        }
    }

    @Access(level = UserType.USER)
    @GetMapping( "/list/{storeId}")
    public ResponseEntity<String> listOrders(HttpServletRequest request, @PathVariable Long storeId) throws JsonProcessingException {
        String token = request.getHeader("TOKEN");
        if (token == null) {
            return ResponseEntity.notFound().build();
        }
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<Order> orders = orderService.listAllOrders(user.getId(), null, null).stream()
                .map(Order::getId)
                .map(orderService::getFullOrderById)
                .filter(
                        e -> e.getItems().stream()
                                .map(OrderItem::getGoodsId)
                                .map(storeService::getGoodsById)
                                .anyMatch(e1 -> e1.getStoreId().equals(storeId))
                ).peek(
                        e -> e.setItems(
                                e.getItems().stream()
                                        .map(e1 -> {
                                            OrderItem item = new OrderItem() {
                                                public String getGoodsName() {
                                                    return storeService.getGoodsById(e1.getGoodsId()).getName();
                                                }
                                            };
                                            item.setOrderId(e1.getOrderId());
                                            item.setAmount(e1.getAmount());
                                            item.setGoodsId(e1.getGoodsId());
                                            item.setPrice(e1.getPrice());
                                            return item;
                                        })
                                        .toList()
                        )
                ).toList();
        ObjectMapper objectMapper = new ObjectMapper();
        return ResponseEntity.ok(objectMapper.writeValueAsString(orders));
    }

    @Access(level = UserType.ADMIN)
    @GetMapping( "/statistics/{storeId}")
    public ResponseEntity<String> listOrders(@PathVariable Long storeId) throws JsonProcessingException {
        List<Goods> statistics = this.orderService.listAllOrders(null, null, null).stream()
                .map(Order::getId)
                .map(this.orderService::getFullOrderById)
                .flatMap(e -> e.getItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getGoodsId,
                        Collectors.summingInt(OrderItem::getAmount)
                ))
                .entrySet().stream()
                .map(e -> {
                    Goods goods = storeService.getGoodsById(e.getKey());
                    goods.setQuantity(e.getValue());
                    return goods;
                })
                .filter(e -> storeId == null || e.getStoreId().equals(storeId))
                .peek(e -> e.setId(null))
                .peek(e -> e.setPhotos(null))
                .peek(e -> e.setStoreId(null))
                .peek(e -> e.setPrice(null))
                .peek(e -> e.setHidden(null))
                .toList();
        ObjectMapper objectMapper = new ObjectMapper();
        return ResponseEntity.ok(objectMapper.writeValueAsString(statistics));
    }
}
