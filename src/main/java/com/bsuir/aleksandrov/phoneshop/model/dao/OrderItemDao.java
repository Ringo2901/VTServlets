package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;

import java.util.List;

public interface OrderItemDao {
    List<OrderItem> getOrderItems (Long key);
}
