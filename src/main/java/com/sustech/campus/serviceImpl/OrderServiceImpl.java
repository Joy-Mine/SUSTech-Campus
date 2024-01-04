package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.Goods;
import com.sustech.campus.entity.Order;
import com.sustech.campus.entity.OrderItem;
import com.sustech.campus.enums.OrderStatus;
import com.sustech.campus.mapper.GoodsMapper;
import com.sustech.campus.mapper.OrderItemMapper;
import com.sustech.campus.mapper.OrderMapper;
import com.sustech.campus.mapper.UserMapper;
import com.sustech.campus.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private UserMapper userMapper;

    private GoodsMapper goodsMapper;

    private OrderMapper orderMapper;

    private OrderItemMapper orderItemMapper;

    @Autowired
    public OrderServiceImpl(UserMapper userMapper, GoodsMapper goodsMapper,
                            OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.userMapper = userMapper;
        this.goodsMapper = goodsMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackForClassName = {"Exception", "RuntimeException"})
    public Order createOrder(Long purchaser, List<Long> goodsIds, List<Integer> amounts) {
        if (goodsIds.size() != amounts.size()) {
            return null;
        }
        if (this.userMapper.selectById(purchaser) == null) {
            return null;
        }
        List<BigDecimal> prices = new ArrayList<>();
        for (int i = 0; i < goodsIds.size(); ++i) {
            Goods goods = this.goodsMapper.selectById(goodsIds.get(i));
            if (goods == null || goods.getQuantity().compareTo(amounts.get(i)) < 0) {
                return null;
            }
            prices.add(goods.getPrice());
        }
        Order order = new Order();
        order.setPurchaser(purchaser);
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setTime(System.currentTimeMillis());
        order.setItems(new ArrayList<>());
        this.orderMapper.insert(order);
        for (int i = 0; i < goodsIds.size(); ++i) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setGoodsId(goodsIds.get(i));
            item.setAmount(amounts.get(i));
            item.setPrice(prices.get(i));
            this.orderItemMapper.insert(item);
            order.getItems().add(item);
        }
        updateGoodsQuantity(order, false);
        return order;
    }

    @Override
    public List<Order> listAllOrders(Long purchaser, Long from, Long to) {
        QueryWrapper<Order> wrapper;
        if (purchaser == null && from == null && to == null) {
            wrapper = null;
        } else {
            wrapper = new QueryWrapper<>();
            if (purchaser != null) {
                wrapper.eq("purchaser", purchaser);
            }
            if (from != null) {
                wrapper.ge("time", from);
            }
            if (to != null) {
                wrapper.le("time", to);
            }
        }
        return this.orderMapper.selectList(wrapper);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return this.orderMapper.selectById(orderId);
    }

    @Override
    public boolean orderExists(Long orderId) {
        return this.getOrderById(orderId) != null;
    }

    @Override
    public Order getFullOrderById(Long orderId) {
        Order order = this.getOrderById(orderId);
        if (order == null) {
            return null;
        }
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("orderId", orderId);
        List<OrderItem> orderItems = this.orderItemMapper.selectList(wrapper);
        order.setItems(orderItems);
        return order;
    }

//    @Override
//    public boolean orderPaid(Long orderId) {
//        return this.changeOrderStatus(orderId, OrderStatus.WAITING_PAYMENT, OrderStatus.PAID);
//    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackForClassName = {"Exception", "RuntimeException"})
    public boolean orderCancelled(Long orderId) {
        if (!this.changeOrderStatus(orderId, OrderStatus.WAITING_PAYMENT, OrderStatus.CANCELLED)) {
            return false;
        }
        Order order = getFullOrderById(orderId);
        updateGoodsQuantity(order, true);
        return true;
    }

    @Override
    public boolean orderFinished(Long orderId) {
        return this.changeOrderStatus(orderId, OrderStatus.WAITING_PAYMENT, OrderStatus.FINISHED);
    }

//    @Override
//    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackForClassName = {"Exception", "RuntimeException"})
//    public boolean orderRefunded(Long orderId) {
//        if (!this.changeOrderStatus(orderId, OrderStatus.PAID, OrderStatus.REFUNDED)) {
//            return false;
//        }
//        Order order = getFullOrderById(orderId);
//        updateGoodsQuantity(order, true);
//        return true;
//    }

    @Override
    public boolean deleteOrder(Long orderId) {
        if (!orderExists(orderId)) {
            return false;
        }
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("orderId", orderId);
        orderItemMapper.delete(wrapper);
        orderMapper.deleteById(orderId);
        return true;
    }

    private boolean changeOrderStatus(Long orderId, OrderStatus fromStatus, OrderStatus toStatus) {
        Order order = this.getOrderById(orderId);
        if (order == null || !order.getStatus().equals(fromStatus)) {
            return false;
        }
        order.setStatus(toStatus);
        this.orderMapper.updateById(order);
        return true;
    }

    private void updateGoodsQuantity(Order order, boolean isRollBack) {
        for (OrderItem item : order.getItems()) {
            Goods goods = goodsMapper.selectById(item.getGoodsId());
            Integer oldQuantity = goods.getQuantity();
            if (isRollBack) {
                goods.setQuantity(oldQuantity + item.getAmount());
            } else {
                goods.setQuantity(oldQuantity - item.getAmount());
            }
            goodsMapper.updateById(goods);
        }
    }
}
