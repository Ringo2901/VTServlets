package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcOrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.model.service.OrderService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.HttpSessionCartService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.OrderServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrderPageServlet extends HttpServlet {
    private OrderService orderService;
    private CartService cartService;
    private static final String ODER_ATTRIBUTE = "order";
    private static final String ORDER_JSP = "/WEB-INF/pages/orderPage.jsp";
    private final String POSSIBLE_ERROR_MESSAGE_NO_FILLING = "This field must be filled in!";
    private final String POSSIBLE_ERROR_MESSAGE_HAS_ERRORS_PHONE = "There were some errors in phone number!\nFormat: +(375)(29/44/25/33)(xxxxxxx)";
    private final String PHONE_VALIDATION_REG_EXP = "^(\\+375)(29|25|44|33)(\\d{3})(\\d{2})(\\d{2})$";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderService = new OrderServiceImpl();
        cartService = HttpSessionCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ODER_ATTRIBUTE, orderService.createOrder(cartService.getCart(request)));
        request.getRequestDispatcher(ORDER_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<Integer, String> errorsMap = new HashMap<>();
        Order order = fillClientData(request, errorsMap);
        if (errorsMap.isEmpty()) {
            try {
                orderService.placeOrder(order, request);
            } catch (OutOfStockException exception) {
                request.setAttribute("order", orderService.createOrder(cartService.getCart(request)));
                errorsMap.put(0, exception.getMessage());
                request.setAttribute("errorsMap", errorsMap);
                doGet(request, response);
            }
            if (errorsMap.isEmpty()) {
                response.sendRedirect("/orderOverview/" + order.getSecureID());
            }
        } else {
            request.setAttribute("errorsMap", errorsMap);
            request.setAttribute("order", orderService.createOrder(cartService.getCart(request)));
            doGet(request, response);
        }
    }

    private Order fillClientData(HttpServletRequest request, Map<Integer, String> errorsMap) {
        Order order = orderService.createOrder(cartService.getCart(request));
        String field = request.getParameter("firstName");
        if (field == null || field.isEmpty()) {
            errorsMap.put(1, POSSIBLE_ERROR_MESSAGE_NO_FILLING);
        } else {
            order.setFirstName(field);
        }
        field = request.getParameter("lastName");
        if (field == null || field.isEmpty()) {
            errorsMap.put(2, POSSIBLE_ERROR_MESSAGE_NO_FILLING);
        } else {
            order.setLastName(field);
        }
        field = request.getParameter("deliveryAddress");
        if (field == null || field.isEmpty()) {
            errorsMap.put(3, POSSIBLE_ERROR_MESSAGE_NO_FILLING);
        } else {
            order.setDeliveryAddress(field);
        }
        field = request.getParameter("contactPhoneNo");
        if (field == null || field.isEmpty()) {
            errorsMap.put(4, POSSIBLE_ERROR_MESSAGE_NO_FILLING);
        } else {
            if (!field.matches(PHONE_VALIDATION_REG_EXP)) {
                errorsMap.put(4, POSSIBLE_ERROR_MESSAGE_HAS_ERRORS_PHONE);
            } else {
                order.setContactPhoneNo(field);
            }
        }
        order.setAdditionalInformation(request.getParameter("additionalInformation"));
        return order;
    }
}
