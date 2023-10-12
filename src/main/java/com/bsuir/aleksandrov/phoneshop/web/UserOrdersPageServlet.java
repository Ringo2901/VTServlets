package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcOrderDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.http.HttpRequest;

public class UserOrdersPageServlet extends HttpServlet {
    private static final String ADMIN_ORDERS_PAGE_JSP = "/WEB-INF/pages/userOrders.jsp";
    private OrderDao orderDao;
    private static final String ORDERS_ATTRIBUTE = "orders";
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = JdbcOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ORDERS_ATTRIBUTE, orderDao.findOrdersByLogin(request.getSession().getAttribute("login").toString()));
        request.getRequestDispatcher(ADMIN_ORDERS_PAGE_JSP).forward(request, response);
    }
}