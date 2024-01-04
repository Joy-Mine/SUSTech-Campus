package com.sustech.campus.service;

import com.sustech.campus.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    /**
     * @param purchaser
     * @param goodsIds
     * @param amounts
     * @return null if failed to create the order, otherwise  the order
     */
    Order createOrder(Long purchaser, List<Long> goodsIds, List<Integer> amounts);

    /**
     * @param purchaser only return the orders of the given purchaser, pass null to disable this filter
     * @param from only return orders created no earlier than the given time, pass null to disable this filter
     * @param to only return orders created no later than the given time, pass null to disable this filter
     * @return all orders that satisfy the requirements above
     */
    List<Order> listAllOrders(Long purchaser, Long from, Long to);

    /**
     * @param orderId
     * @return null if the order with the given id does not exist, otherwise, an Order instance whose
     * items field is null
     */
    Order getOrderById(Long orderId);

    /**
     * @param orderId
     * @return whether the order with the given id exists
     */
    boolean orderExists(Long orderId);

    /**
     * @param orderId
     * @return null if the order with the given id does not exist, otherwise, an Order instance whose
     * items field is filled
     */
    Order getFullOrderById(Long orderId);

//    /**
//     * @param orderId
//     * @return false if failed to change the status of the order with the given id, otherwise, true
//     */
//    boolean orderPaid(Long orderId);

    /**
     * @param orderId
     * @return false if failed to change the status of the order with the given id, otherwise, true
     */
    boolean orderCancelled(Long orderId);

    /**
     * @param orderId
     * @return false if failed to change the status of the order with the given id, otherwise, true
     */
    boolean orderFinished(Long orderId);

//    /**
//     * @param orderId
//     * @return false if failed to change the status of the order with the given id, otherwise, true
//     */
//    boolean orderRefunded(Long orderId);

    /**
     * this method is only for testing
     * @param orderId
     * @return false if failed to delete the order with the given id, otherwise, true
     */
    boolean deleteOrder(Long orderId);
}
