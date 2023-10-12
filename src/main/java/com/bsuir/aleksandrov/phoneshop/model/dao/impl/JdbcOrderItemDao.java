package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.OrderItemDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItemsExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcOrderItemDao implements OrderItemDao {
    private static final Logger log = Logger.getLogger(OrderItemDao.class);
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
            log.log(Level.INFO, "Found orderItems in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            log.log(Level.ERROR, "Error in getOrderItems", ex);
        } finally {
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
