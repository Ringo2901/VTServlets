package com.bsuir.aleksandrov.phoneshop.model.entities.order;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderItemDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcOrderItemDao;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdersExtractor {
    public List<Order> extractData(ResultSet resultSet) throws SQLException, DaoException {
        List<Order> orders = new ArrayList<>();
        OrderItemDao orderItemDao = new JdbcOrderItemDao();
        while (resultSet.next()) {
            Order order = new Order();
            order.setId(resultSet.getLong("id"));
            order.setSecureID(resultSet.getString("secureID"));
            order.setSubtotal(resultSet.getBigDecimal("subtotal"));
            order.setDeliveryPrice(resultSet.getBigDecimal("deliveryPrice"));
            order.setTotalPrice(resultSet.getBigDecimal("totalPrice"));
            order.setFirstName(resultSet.getString("firstName"));
            order.setLastName(resultSet.getString("lastName"));
            order.setDeliveryAddress(resultSet.getString("deliveryAddress"));
            order.setContactPhoneNo(resultSet.getString("contactPhoneNo"));
            order.setAdditionalInformation(resultSet.getString("additionalInformation"));
            order.setStatus(OrderStatus.fromString(resultSet.getString("status")));
            order.setOrderItems(orderItemDao.getOrderItems(order.getId()));
            order.setDate(resultSet.getDate("date"));
            order.setTime(resultSet.getTime("time"));
            order.setLogin(resultSet.getString("login"));
            orders.add(order);
        }
        return orders;
    }
}
