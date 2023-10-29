package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.model.service.OrderService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.HttpSessionCartService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.OrderServiceImpl;
import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class OrderPageCommand implements ICommand {
    private static final String ODER_ATTRIBUTE = "order";
    private static final String SECURE_ID_ATTRIBUTE = "secureId";
    private OrderService orderService = OrderServiceImpl.getInstance();
    private CartService cartService = HttpSessionCartService.getInstance();
    private final String PHONE_VALIDATION_REG_EXP = "^(\\+375)(29|25|44|33)(\\d{3})(\\d{2})(\\d{2})$";

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (request.getMethod().equals("GET")) {
            request.setAttribute(ODER_ATTRIBUTE, orderService.createOrder(cartService.getCart(request.getSession())));
            return JspPageName.ORDER_JSP;
        } else {
            Map<Integer, String> errorsMap = new HashMap<>();
            Order order = fillClientData(request, errorsMap);
            if (errorsMap.isEmpty()) {
                try {
                    orderService.placeOrder(order, request);
                } catch (OutOfStockException exception) {
                    request.setAttribute("order", orderService.createOrder(cartService.getCart(request.getSession())));
                    errorsMap.put(0, exception.getMessage());
                    request.setAttribute("errorsMap", errorsMap);
                    return "http://localhost:8080/?command=order";
                }
                if (errorsMap.isEmpty()) {
                    request.setAttribute(SECURE_ID_ATTRIBUTE, order.getSecureID());
                    return "http://localhost:8080/?command=order_overview";
                }
            } else {
                request.setAttribute("errorsMap", errorsMap);
                request.setAttribute("order", orderService.createOrder(cartService.getCart(request.getSession())));
                return "http://localhost:8080/?command=order";
            }
        }
        return JspPageName.ORDER_JSP;
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
