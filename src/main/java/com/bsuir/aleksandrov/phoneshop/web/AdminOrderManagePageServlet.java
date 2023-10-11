package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcOrderDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.Order;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;
import com.bsuir.aleksandrov.phoneshop.model.service.OrderService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.OrderServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AdminOrderManagePageServlet extends HttpServlet {
    private static final String ADMIN_ORDERS_PAGE_JSP = "/WEB-INF/pages/adminOrderManagePage.jsp";
    private static final String ORDER_NOT_FOUND_PAGE_JSP = "/WEB-INF/pages/OrderNotFoundPage.jsp";
    private static final String ORDER_ATTRIBUTE = "order";
    private OrderDao orderDao;
    private OrderService orderService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = JdbcOrderDao.getInstance();
        orderService = OrderServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Order order = orderDao.getById(Long.parseLong(request.getPathInfo().substring(1))).orElse(null);
        if (order != null) {
            request.setAttribute(ORDER_ATTRIBUTE, order);
            request.getRequestDispatcher(ADMIN_ORDERS_PAGE_JSP).forward(request, response);
        } else {
            request.getRequestDispatcher(ORDER_NOT_FOUND_PAGE_JSP).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = Long.parseLong(request.getPathInfo().substring(1));
        OrderStatus newStatus = OrderStatus.fromString(request.getParameter("status"));
        if (newStatus != null) {
            orderService.changeOrderStatus(id, newStatus);
            request.setAttribute("successMessage", "Successfully change status");
        } else {
            request.setAttribute("errorMessage", "There was an error");
        }
        request.setAttribute("order", orderDao.getById(id).orElse(null));
        doGet(request, response);
    }
}
