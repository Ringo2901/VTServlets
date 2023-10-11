package com.bsuir.aleksandrov.phoneshop.model.service;

import com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import jakarta.servlet.http.HttpServletRequest;

public interface CartService {
    Cart getCart(HttpServletRequest request);
    void add (Cart cart, Long productId, int quantity, HttpServletRequest request) throws OutOfStockException;
    void update(Cart cart, Long productId, int quantity, HttpServletRequest request) throws OutOfStockException;

    void delete(Cart cart, Long productId, HttpServletRequest request);

    void reCalculateCart(Cart cartToRecalculate);

    void clear (HttpServletRequest request);

    void remove(HttpServletRequest request, Long phoneId);

}
