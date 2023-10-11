package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrdersExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcOrderDao implements OrderDao {
    private static final String GET_ORDER_BY_ID = "SELECT * FROM orders WHERE id = ?";
    private static final String GET_ORDER_BY_SECURE_ID = "SELECT * FROM orders WHERE secureID = ?";
    private static final String SAVE_ORDER = "INSERT INTO orders (secureID, subtotal, deliveryPrice, " +
            "totalPrice, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInformation) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CHANGE_STATUS = "UPDATE orders SET status = ? WHERE id = ?";
    private static final String ADD_ORDER2ITEM = "INSERT INTO order2item (orderId, phoneId, quantity) " +
            "VALUES (?, ?, ?)";

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
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(ADD_ORDER2ITEM);
            statement.setLong(1, orderId);
            statement.setLong(2, phoneId);
            statement.setInt(3, quantity);
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
