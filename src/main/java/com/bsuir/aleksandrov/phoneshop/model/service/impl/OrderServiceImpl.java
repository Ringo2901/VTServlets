package com.bsuir.aleksandrov.phoneshop.model.service.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.StockDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcOrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcStockDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.model.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {
    private CartService cartService = HttpSessionCartService.getInstance();
    private StockDao stockDao = JdbcStockDao.getInstance();
    private OrderDao orderDao = JdbcOrderDao.getInstance();
    public Order createOrder(Cart cart) {
        Order order = new Order();
        BigDecimal deliveryPrice = BigDecimal.valueOf(10);
        order.setDeliveryPrice(deliveryPrice);
        order.setSubtotal(cart.getTotalCost());
        order.setTotalPrice(order.getSubtotal().add(order.getDeliveryPrice()));
        fillOrderItems(order, cart);
        return order;
    }

    @Override
    public void placeOrder(final Order order, HttpServletRequest request) throws OutOfStockException {
        checkStock(request, order);
        order.setStatus(OrderStatus.NEW);
        order.getOrderItems().stream()
                .forEach(item -> stockDao.reserve(item.getPhone().getId(), item.getQuantity()));
        order.setSecureID(UUID.randomUUID().toString());
        orderDao.save(order);
        cartService.clear(request);
    }

    private void fillOrderItems(Order order, Cart cart) {
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setPhone(cartItem.getPhone());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
    }

    private void checkStock(HttpServletRequest request, final Order order) throws OutOfStockException {
        List<OrderItem> outOfStockItems = order.getOrderItems().stream()
                .filter(item -> stockDao.availableStock(item.getPhone().getId()) - item.getQuantity() < 0)
                .collect(Collectors.toList());
        if (!outOfStockItems.isEmpty()) {
            StringBuilder outOfStockModels = new StringBuilder();
            outOfStockItems.stream().forEach(item -> {
                outOfStockModels.append(item.getPhone().getModel() + "; ");
                cartService.remove(request, item.getPhone().getId());
            });
            throw new OutOfStockException("Some of items out of stock (" + outOfStockModels + "). They deleted from cart.");
        }
    }
}
