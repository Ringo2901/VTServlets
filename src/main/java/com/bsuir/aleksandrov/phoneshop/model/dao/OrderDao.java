package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * @author nekit
 * @version 1.0
 */
public interface OrderDao {
    /**
     * Find order from database using id
     * @param key - id of order
     * @return order
     */
    Optional<Order> getById(Long key);

    /**
     * Find order from database using secureId
     * @param secureID - secureId of order
     * @return - order
     */

    Optional<Order> getBySecureID(String secureID);

    /**
     * Save order in database
     * @param order - order to save
     */
    void save(Order order);

    /**
     * Find all orders in database
     * @return List of orders
     */
    List<Order> findOrders();

    /**
     * Find orders of user with login
     * @param login login to find
     * @return List of orders
     */

    List<Order> findOrdersByLogin(String login);

    /**
     * Change status of order
     * @param id id of order
     * @param status new status of order
     */
    void changeStatus(Long id, OrderStatus status);
}
