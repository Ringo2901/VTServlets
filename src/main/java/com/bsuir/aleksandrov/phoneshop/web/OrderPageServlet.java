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
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class OrderPageServlet extends HttpServlet {
    private OrderService orderService;
    private CartService cartService;
    private static final String ODER_ATTRIBUTE = "order";
    private static final String ORDER_JSP = "/WEB-INF/pages/orderPage.jsp";
    private final String PHONE_VALIDATION_REG_EXP = "^(\\+375)(29|25|44|33)(\\d{3})(\\d{2})(\\d{2})$";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderService = new OrderServiceImpl();
        cartService = HttpSessionCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ODER_ATTRIBUTE, orderService.createOrder(cartService.getCart(request.getSession())));
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
                request.setAttribute("order", orderService.createOrder(cartService.getCart(request.getSession())));
                errorsMap.put(0, exception.getMessage());
                request.setAttribute("errorsMap", errorsMap);
                doGet(request, response);
            }
            if (errorsMap.isEmpty()) {
                response.sendRedirect("/orderOverview/" + order.getSecureID());
            }
        } else {
            request.setAttribute("errorsMap", errorsMap);
            request.setAttribute("order", orderService.createOrder(cartService.getCart(request.getSession())));
            doGet(request, response);
        }
    }

    private Order fillClientData(HttpServletRequest request, Map<Integer, String> errorsMap) {
        Order order = orderService.createOrder(cartService.getCart(request.getSession()));
        String field = request.getParameter("firstName");
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null){
            lang = "en";
        }
        Locale locale = new Locale(lang.toString());
        ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
        if (field == null || field.isEmpty()) {
            errorsMap.put(1, rb.getString("POSSIBLE_ERROR_MESSAGE_NO_FILLING"));
        } else {
            order.setFirstName(field);
        }
        field = request.getParameter("lastName");
        if (field == null || field.isEmpty()) {
            errorsMap.put(2,  rb.getString("POSSIBLE_ERROR_MESSAGE_NO_FILLING"));
        } else {
            order.setLastName(field);
        }
        field = request.getParameter("deliveryAddress");
        if (field == null || field.isEmpty()) {
            errorsMap.put(3,  rb.getString("POSSIBLE_ERROR_MESSAGE_NO_FILLING"));
        } else {
            order.setDeliveryAddress(field);
        }
        field = request.getParameter("contactPhoneNo");
        if (field == null || field.isEmpty()) {
            errorsMap.put(4,  rb.getString("POSSIBLE_ERROR_MESSAGE_NO_FILLING"));
        } else {
            if (!field.matches(PHONE_VALIDATION_REG_EXP)) {
                errorsMap.put(4, rb.getString("POSSIBLE_ERROR_MESSAGE_HAS_ERRORS_PHONE"));
            } else {
                order.setContactPhoneNo(field);
            }
        }
        order.setAdditionalInformation(request.getParameter("additionalInformation"));
        return order;
    }
}
