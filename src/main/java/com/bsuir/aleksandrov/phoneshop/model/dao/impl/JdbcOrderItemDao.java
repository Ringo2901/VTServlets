package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderItemDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItemsExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcOrderItemDao implements OrderItemDao {
    private static final String GET_ORDER_ITEMS = "SELECT * FROM order2item WHERE orderId = ?";
    private ConnectionPool connectionPool = ConnectionPool.getInstance();
    private OrderItemsExtractor orderItemsExtractor = new OrderItemsExtractor();

    @Override
    public List<OrderItem> getOrderItems(final Long key) {
        List<OrderItem> orderItems = null;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_ORDER_ITEMS);
            statement.setLong(1, key);
            ResultSet resultSet = statement.executeQuery();
            orderItems = orderItemsExtractor.extractData(resultSet);
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
        return orderItems;
    }
}
