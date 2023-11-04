package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcOrderDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.ServiceException;
import com.bsuir.aleksandrov.phoneshop.model.service.OrderService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.OrderServiceImpl;
import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class AdminOrderOverviewCommand implements ICommand {
    private static final String ORDER_ATTRIBUTE = "order";
    private static final String SUCCESS_ATTRIBUTE = "successMessage";
    private static final String ERROR_ATTRIBUTE = "errorMessage";
    private OrderService orderService = OrderServiceImpl.getInstance();

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (request.getMethod().equals("GET")){
            Order order;
            try {
                order = orderService.getById(Long.parseLong(request.getParameter("orderId"))).orElse(null);
            } catch (ServiceException e) {
                throw new CommandException(e.getMessage());
            }
            if (order != null) {
                request.setAttribute(ORDER_ATTRIBUTE, order);
                return JspPageName.ADMIN_ORDER_MANAGE_PAGE_JSP;
            } else {
                return JspPageName.ORDER_NOT_FOUND_PAGE_JSP;
            }
        } else{
            change_status(request);
            return request.getHeader("Referer");
        }
    }

    private void change_status (HttpServletRequest request) throws CommandException {
        Long id = Long.parseLong(request.getParameter("orderId"));
        OrderStatus newStatus = OrderStatus.fromString(request.getParameter("status"));
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null){
            lang = "en";
        }
        Locale locale = new Locale(lang.toString());
        ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
        if (newStatus != null) {
            try {
                orderService.changeOrderStatus(id, newStatus);
            } catch (ServiceException e) {
                throw new CommandException(e.getMessage());
            }
            request.setAttribute(SUCCESS_ATTRIBUTE, rb.getString("status_change_success"));
        } else {
            request.setAttribute(ERROR_ATTRIBUTE, rb.getString("error_message"));
        }
        try {
            request.setAttribute(ORDER_ATTRIBUTE, orderService.getById(id).orElse(null));
        } catch (ServiceException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
