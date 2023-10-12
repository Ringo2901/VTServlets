package com.bsuir.aleksandrov.phoneshop.model.service;

import com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    Order createOrder(Cart cart);

    void placeOrder(Order order, HttpServletRequest request) throws OutOfStockException;

    void changeOrderStatus(Long id, OrderStatus status);
}
