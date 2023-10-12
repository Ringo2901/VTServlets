package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrdersExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;

import java.sql.*;
import java.util.List;
import java.util.Optional;

public class JdbcOrderDao implements OrderDao {
    private static final String GET_ORDER_BY_ID = "SELECT * FROM orders WHERE id = ?";
    private static final String GET_ORDER_BY_SECURE_ID = "SELECT * FROM orders WHERE secureID = ?";
    private static final String SAVE_ORDER = "INSERT INTO orders (secureID, subtotal, deliveryPrice, " +
            "totalPrice, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInformation, date, time, login) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CHANGE_STATUS = "UPDATE orders SET status = ? WHERE id = ?";
    private static final String ADD_ORDER2ITEM = "INSERT INTO order2item (orderId, phoneId, quantity) " +
            "VALUES (?, ?, ?)";
    private static final String GET_ALL_ORDERS = "SELECT * FROM orders";
    private static final String GET_ALL_ORDERS_BY_LOGIN = "SELECT * FROM orders WHERE login = ?";

    private OrdersExtractor ordersExtractor = new OrdersExtractor();
    private ConnectionPool connectionPool = ConnectionPool.getInstance();

    private static volatile OrderDao instance;

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

    @Override
    public Optional<Order> getById(final Long key) {
        Optional<Order> order = null;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_ORDER_BY_ID);
            statement.setLong(1, key);
            ResultSet resultSet = statement.executeQuery();
            order = ordersExtractor.extractData(resultSet).stream().findAny();
            // LOGGER.log(Level.INFO, "Found {0} phones in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // LOGGER.log(Level.SEVERE, "Error in findProducts", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return order;
    }

    @Override
    public Optional<Order> getBySecureID(String secureID) {
        Optional<Order> order = null;
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_ORDER_BY_SECURE_ID);
            statement.setString(1, secureID);
            ResultSet resultSet = statement.executeQuery();
            order = ordersExtractor.extractData(resultSet).stream().findAny();
            // LOGGER.log(Level.INFO, "Found {0} phones in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // LOGGER.log(Level.SEVERE, "Error in findProducts", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return order;
    }
    @Override
    public List<Order> findOrders() {
        List<Order> orders = null;
        Statement statement = null;
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_ORDERS);
            orders = ordersExtractor.extractData(resultSet);
            // LOGGER.log(Level.INFO, "Found {0} phones in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // LOGGER.log(Level.SEVERE, "Error in findProducts", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return orders;
    }

    @Override
    public List<Order> findOrdersByLogin(String login) {
        List<Order> orders = null;
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_ALL_ORDERS_BY_LOGIN);
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            orders = ordersExtractor.extractData(resultSet);
            // LOGGER.log(Level.INFO, "Found {0} phones in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // LOGGER.log(Level.SEVERE, "Error in findProducts", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return orders;
    }

    @Override
    public void changeStatus(Long id, OrderStatus status) {
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(CHANGE_STATUS);
            statement.setLong(2, id);
            statement.setString(1, status.toString());
            statement.executeUpdate();
            // LOGGER.log(Level.INFO, "Found {0} phones in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // LOGGER.log(Level.SEVERE, "Error in findProducts", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    @Override
    public void save(final Order order) {
        Connection conn = null;
        PreparedStatement statement = null;

        try {
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
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    private void addOrderItem(Connection conn, Long orderId, Long phoneId, int quantity) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(ADD_ORDER2ITEM)) {
            statement.setLong(1, orderId);
            statement.setLong(2, phoneId);
            statement.setInt(3, quantity);
            statement.executeUpdate();
        }
    }
}
