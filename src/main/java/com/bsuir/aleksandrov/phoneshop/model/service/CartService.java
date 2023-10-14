package com.bsuir.aleksandrov.phoneshop.model.service;

import com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import jakarta.servlet.http.HttpServletRequest;
/**
 * @author nekit
 * @version 1.0
 */
public interface CartService {
    /**
     * Get cart from request
     * @param request request with cart
     * @return  cart
     */
    Cart getCart(HttpServletRequest request);

    /**
     *
     * @param cart cart to add
     * @param productId id of product to add
     * @param quantity quantity of product to add
     * @param request request with cart
     * @throws OutOfStockException throws when phone out of stock when adding
     */
    void add(Cart cart, Long productId, int quantity, HttpServletRequest request) throws OutOfStockException;

    /**
     * Update phone in cart
     * @param cart cart to update
     * @param productId id of phone to update
     * @param quantity quantity of phone to update
     * @param request request with cart
     * @throws OutOfStockException throws when phone out of stock when updating
     */

    void update(Cart cart, Long productId, int quantity, HttpServletRequest request) throws OutOfStockException;

    /**
     * Delete phone from cart
     * @param cart cart to delete
     * @param productId id of phone to delete
     * @param request request with cart
     */

    void delete(Cart cart, Long productId, HttpServletRequest request);

    /**
     * Recalculate cart total price
     * @param cartToRecalculate cat to recalculate
     */

    void reCalculateCart(Cart cartToRecalculate);

    /**
     * Clear cart
     * @param request request with cart
     */
    void clear(HttpServletRequest request);

    /**
     * Remove item from cart
     * @param request request with cart
     * @param phoneId id of phone to remove
     */

    void remove(HttpServletRequest request, Long phoneId);

}
