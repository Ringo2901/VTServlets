package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcOrderDao;
import com.bsuir.aleksandrov.phoneshop.model.service.OrderService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.OrderServiceImpl;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class OrderOverviewServlet extends HttpServlet {
    private OrderDao orderDao;
    private static final String ORDER_ATTRIBUTE = "order";
    private static final String ORDER_PATH_ATTRIBUTE = "/WEB-INF/pages/orderOverviewPage.jsp";


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = JdbcOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ORDER_ATTRIBUTE, orderDao.getBySecureID(request.getPathInfo().substring(1)).orElse(null));
        request.getRequestDispatcher(ORDER_PATH_ATTRIBUTE).forward(request, response);
    }
}
