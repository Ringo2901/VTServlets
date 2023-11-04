package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;

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
    Optional<Order> getById(Long key) throws DaoException;

    /**
     * Find order from database using secureId
     * @param secureID - secureId of order
     * @return - order
     */

    Optional<Order> getBySecureID(String secureID) throws DaoException;

    /**
     * Save order in database
     * @param order - order to save
     */
    void save(Order order) throws DaoException;

    /**
     * Find all orders in database
     * @return List of orders
     */
    List<Order> findOrders() throws DaoException;

    /**
     * Find orders of user with login
     * @param login login to find
     * @return List of orders
     */

    List<Order> findOrdersByLogin(String login) throws DaoException;

    /**
     * Change status of order
     * @param id id of order
     * @param status new status of order
     */
    void changeStatus(Long id, OrderStatus status) throws DaoException;
}
