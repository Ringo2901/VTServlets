package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;

import java.util.List;

/**
 * @author nekit
 * @version 1.0
 */
public interface OrderItemDao {
    /**
     * Find items from order
     * @param key key of order
     * @return List of OrderItems from order
     */
    List<OrderItem> getOrderItems(Long key);
}
