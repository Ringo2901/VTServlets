package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderItemDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItemsExtractor;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Using jdbc to work with order items
 *
 * @author nekit
 * @version 1.0
 */
public class JdbcOrderItemDao implements OrderItemDao {
    /**
     * Instance of logger
     */
    private static final Logger log = Logger.getLogger(OrderItemDao.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * SQL query for find phones from database by order
     */
    private static final String GET_ORDER_ITEMS = "SELECT * FROM order2item WHERE orderId = ?";
    /**
     * Instance of connection pool
     */
    private ConnectionPool connectionPool = ConnectionPool.getInstance();
    /**
     * Instance of OrderItemsExtractor
     */
    private OrderItemsExtractor orderItemsExtractor = new OrderItemsExtractor();

    /**
     * Get orderItems from database by id of order
     *
     * @param key key of order
     * @return List of OrderItems
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public List<OrderItem> getOrderItems(final Long key) throws DaoException {
        List<OrderItem> orderItems = null;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            lock.readLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_ORDER_ITEMS);
            statement.setLong(1, key);
            ResultSet resultSet = statement.executeQuery();
            orderItems = orderItemsExtractor.extractData(resultSet);
            log.log(Level.INFO, "Found orderItems in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in getOrderItems", ex);
            throw new DaoException("Error in process of getting orderItems");
        } finally {
            lock.readLock().unlock();
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "Error in closing statement", ex);
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return orderItems;
    }
}
