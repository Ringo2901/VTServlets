package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrdersExtractor;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Using jdbc to work with order
 *
 * @author nekit
 * @version 1.0
 */
public class JdbcOrderDao implements OrderDao {
    /**
     * Field of logger
     */
    private static final Logger log = Logger.getLogger(OrderDao.class);
    /**
     * SQL query for find order with id
     */
    private static final String GET_ORDER_BY_ID = "SELECT * FROM orders WHERE id = ?";
    /**
     * SQL query for find order with secureID
     */
    private static final String GET_ORDER_BY_SECURE_ID = "SELECT * FROM orders WHERE secureID = ?";
    /**
     * SQL query for save order in database
     */
    private static final String SAVE_ORDER = "INSERT INTO orders (secureID, subtotal, deliveryPrice, " +
            "totalPrice, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInformation, date, time, login) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    /**
     * SQL query for change status of order
     */
    private static final String CHANGE_STATUS = "UPDATE orders SET status = ? WHERE id = ?";
    /**
     * SQL query for adding order items in database
     */
    private static final String ADD_ORDER2ITEM = "INSERT INTO order2item (orderId, phoneId, quantity) " +
            "VALUES (?, ?, ?)";
    /**
     * SQL query for get all orders from database
     */
    private static final String GET_ALL_ORDERS = "SELECT * FROM orders";
    /**
     * SQL query for get all orders from database by login
     */
    private static final String GET_ALL_ORDERS_BY_LOGIN = "SELECT * FROM orders WHERE login = ?";
    /**
     * Field of order extractor
     */
    private OrdersExtractor ordersExtractor = new OrdersExtractor();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * Instance of connection pool
     */
    private ConnectionPool connectionPool = ConnectionPool.getInstance();
    /**
     * Instance of OrderDao
     */
    private static volatile OrderDao instance;

    /**
     * Realisation of Singleton pattern
     *
     * @return instance of OrderDao
     */
    public static OrderDao getInstance() {
        if (instance == null) {
            synchronized (OrderDao.class) {
                if (instance == null) {
                    instance = new JdbcOrderDao();
                }
            }
        }
        return instance;
    }

    /**
     * Get order by id
     *
     * @param key - id of order
     * @return order
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public Optional<Order> getById(final Long key) throws DaoException {
        Optional<Order> order = null;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            lock.readLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_ORDER_BY_ID);
            statement.setLong(1, key);
            ResultSet resultSet = statement.executeQuery();
            order = ordersExtractor.extractData(resultSet).stream().findAny();
            log.log(Level.INFO, "Found order by id in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in getById", ex);
            throw new DaoException("Error in process of getting order");
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
        return order;
    }

    /**
     * Get order by secureId
     *
     * @param secureID - secureId of order
     * @return order
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public Optional<Order> getBySecureID(String secureID) throws DaoException {
        Optional<Order> order = null;
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            lock.readLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_ORDER_BY_SECURE_ID);
            statement.setString(1, secureID);
            ResultSet resultSet = statement.executeQuery();
            order = ordersExtractor.extractData(resultSet).stream().findAny();
            log.log(Level.INFO, "Found orders by secureId in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in getBySecureID", ex);
            throw new DaoException("Error in process of getting order");
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
        return order;
    }

    /**
     * Find orders in database
     *
     * @return List of orders
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public List<Order> findOrders() throws DaoException {
        List<Order> orders;
        Statement statement = null;
        Connection conn = null;
        try {
            lock.readLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_ORDERS);
            orders = ordersExtractor.extractData(resultSet);
            log.log(Level.INFO, "Found all orders in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in findOrders", ex);
            throw new DaoException("Error in process of finding orders");
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
        return orders;
    }

    /**
     * Find orders in database by login
     *
     * @param login login to find
     * @return List of orders
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public List<Order> findOrdersByLogin(String login) throws DaoException {
        List<Order> orders = null;
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            lock.readLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_ALL_ORDERS_BY_LOGIN);
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            orders = ordersExtractor.extractData(resultSet);
            log.log(Level.INFO, "Found orders by login in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in findOrdersByLogin", ex);
            throw new DaoException("Error in process of finding orders");
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
        return orders;
    }

    /**
     * Change status of order
     *
     * @param id     id of order
     * @param status new status of order
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public void changeStatus(Long id, OrderStatus status) throws DaoException {
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            lock.writeLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(CHANGE_STATUS);
            statement.setLong(2, id);
            statement.setString(1, status.toString());
            statement.executeUpdate();
            log.log(Level.INFO, "Status changed in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in changeStatus", ex);
            throw new DaoException("Error in process of changing status");
        } finally {
            lock.writeLock().unlock();
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
    }

    /**
     * Save order in database
     *
     * @param order - order to save
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public void save(final Order order) throws DaoException {
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            lock.writeLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(SAVE_ORDER, PreparedStatement.RETURN_GENERATED_KEYS);

            statement.setString(1, order.getSecureID());
            statement.setBigDecimal(2, order.getSubtotal());
            statement.setBigDecimal(3, order.getDeliveryPrice());
            statement.setBigDecimal(4, order.getTotalPrice());
            statement.setString(5, order.getFirstName());
            statement.setString(6, order.getLastName());
            statement.setString(7, order.getDeliveryAddress());
            statement.setString(8, order.getContactPhoneNo());
            statement.setString(9, order.getAdditionalInformation());
            statement.setDate(10, order.getDate());
            statement.setTime(11, order.getTime());
            statement.setString(12, order.getLogin());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long orderId = generatedKeys.getLong(1);
                statement = conn.prepareStatement(CHANGE_STATUS);
                statement.setString(1, order.getStatus().toString());
                statement.setLong(2, orderId);

                statement.executeUpdate();


                for (OrderItem orderItem : order.getOrderItems()) {
                    addOrderItem(conn, orderId, orderItem.getPhone().getId(), orderItem.getQuantity());
                }
                log.log(Level.INFO, "Order save");
            }
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in order save", ex);
            throw new DaoException("Error in process of saving order");
        } finally {
            lock.writeLock().unlock();
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
    }

    /**
     * Add order item in database
     *
     * @param conn     connection to database
     * @param orderId  id of order
     * @param phoneId  id of phone to add
     * @param quantity quantity of phone to add
     * @throws SQLException exception throws when there were some problems during sql operation
     */
    private void addOrderItem(Connection conn, Long orderId, Long phoneId, int quantity) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(ADD_ORDER2ITEM)) {
            statement.setLong(1, orderId);
            statement.setLong(2, phoneId);
            statement.setInt(3, quantity);
            statement.executeUpdate();
        }
    }
}
