package com.bsuir.aleksandrov.phoneshop.model.service.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.StockDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcOrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcStockDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.ServiceException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.model.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Using to manage orders
 *
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
     *
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
     *
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
     *
     * @param order   order to place
     * @param session session with cart
     * @throws OutOfStockException throws when some products out of stock during placing
     * @throws ServiceException    throws when there is some errors during service method execution
     */
    @Override
    public void placeOrder(final Order order, HttpSession session) throws OutOfStockException, ServiceException {
        checkStock(session, order);
        order.setDate(new Date(Instant.now().toEpochMilli()));
        order.setTime(new Time(Instant.now().toEpochMilli()));
        order.setLogin(session.getAttribute("login").toString());
        order.setStatus(OrderStatus.NEW);
        try {
            for (OrderItem item : order.getOrderItems()) {
                try {
                    stockDao.reserve(item.getPhone().getId(), item.getQuantity());
                } catch (DaoException e) {
                    throw new ServiceException(e.getMessage());
                }
            }

            order.setSecureID(UUID.randomUUID().toString());
            orderDao.save(order);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        cartService.clear(session);
    }

    /**
     * Changing order status in database
     *
     * @param id     id of order
     * @param status new status of order
     * @throws ServiceException throws when there is some errors during service method execution
     */
    @Override
    public void changeOrderStatus(Long id, OrderStatus status) throws ServiceException {
        try {
            orderDao.changeStatus(id, status);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public Optional<Order> getById(Long id) throws ServiceException {
        try {
            return orderDao.getById(id);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * Fill order items from cart to order
     *
     * @param order order to fill
     * @param cart  cart with items
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
     *
     * @param session session with cart to remove in case of out of stock
     * @param order   order to check
     * @throws OutOfStockException throws when some products out of stock during placing
     * @throws ServiceException    throws when there is some errors during service method execution
     */
    private void checkStock(HttpSession session, final Order order) throws OutOfStockException, ServiceException {
        List<OrderItem> outOfStockItems = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {
            try {
                if (stockDao.availableStock(item.getPhone().getId()) - item.getQuantity() < 0) {
                    outOfStockItems.add(item);
                }
            } catch (DaoException e) {
                throw new ServiceException(e.getMessage());
            }
        }
        if (!outOfStockItems.isEmpty()) {
            StringBuilder outOfStockModels = new StringBuilder();
            outOfStockItems.stream().forEach(item -> {
                outOfStockModels.append(item.getPhone().getModel() + "; ");
                cartService.remove(session, item.getPhone().getId());
            });
            throw new OutOfStockException("Some of items out of stock (" + outOfStockModels + "). They deleted from cart.");
        }
    }
}
