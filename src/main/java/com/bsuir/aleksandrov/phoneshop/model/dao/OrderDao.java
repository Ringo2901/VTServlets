package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Optional<Order> getById(Long key);

    Optional<Order> getBySecureID(String secureID);

    void save(Order order);
    List<Order> findOrders();
    void changeStatus(Long id, OrderStatus status);
}
