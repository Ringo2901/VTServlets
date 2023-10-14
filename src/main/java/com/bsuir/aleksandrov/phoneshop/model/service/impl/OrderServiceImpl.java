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
import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * Using to manage orders
 * @author nekit
 * @version 1.0
 */
public class OrderServiceImpl implements OrderService {
    /**
     * Instance of cart service
     */
    private CartService cartService = HttpSessionCartService.getInstance();
    /**
     * Instance of stock dao
     */
    private StockDao stockDao = JdbcStockDao.getInstance();
    /**
     * Instance of order dao
     */
    private OrderDao orderDao = JdbcOrderDao.getInstance();
    /**
     * Instance of OrderService
     */
    private static volatile OrderService instance;

    /**
     * Realisation of Singleton pattern
     * @return instance of orderService
     */
    public static OrderService getInstance() {
        if (instance == null) {
            synchronized (OrderService.class) {
                if (instance == null) {
                    instance = new OrderServiceImpl();
                }
            }
        }
        return instance;
    }

    /**
     * Create empty order and fill order items
     * @param cart cart with items
     * @return order
     */
    public Order createOrder(Cart cart) {
        Order order = new Order();
        BigDecimal deliveryPrice = BigDecimal.valueOf(10);
        order.setDeliveryPrice(deliveryPrice);
        order.setSubtotal(cart.getTotalCost());
        order.setTotalPrice(order.getSubtotal().add(order.getDeliveryPrice()));
        fillOrderItems(order, cart);
        return order;
    }

    /**
     * Place order in database
     * @param order order to place
     * @param request request with cart
     * @throws OutOfStockException throws when some products out of stock during placing
     */
    @Override
    public void placeOrder(final Order order, HttpServletRequest request) throws OutOfStockException {
        checkStock(request, order);
        order.setDate(new Date(Instant.now().toEpochMilli()));
        order.setTime(new Time(Instant.now().toEpochMilli()));
        order.setLogin(request.getSession().getAttribute("login").toString());
        order.setStatus(OrderStatus.NEW);
        order.getOrderItems().stream()
                .forEach(item -> stockDao.reserve(item.getPhone().getId(), item.getQuantity()));
        order.setSecureID(UUID.randomUUID().toString());
        orderDao.save(order);
        cartService.clear(request);
    }

    /**
     * Changing order status in database
     * @param id id of order
     * @param status new status of order
     */
    @Override
    public void changeOrderStatus(Long id, OrderStatus status) {
        orderDao.changeStatus(id, status);
    }

    /**
     * Fill order items from cart to order
     * @param order order to fill
     * @param cart cart with items
     */
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

    /**
     * Check stock of items in order
     * @param request request with cart to remove in case of out of stock
     * @param order order to check
     * @throws OutOfStockException throws when some products out of stock during placing
     */
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
