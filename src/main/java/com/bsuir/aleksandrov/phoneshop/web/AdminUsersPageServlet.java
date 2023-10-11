package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.dao.OrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.UserDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcOrderDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcUserDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.order.OrderStatus;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AdminUsersPageServlet extends HttpServlet {
    private static final String ADMIN_ORDERS_PAGE_JSP = "/WEB-INF/pages/adminUsersPage.jsp";
    private UserDao userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userDao = JdbcUserDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("users", userDao.findAllUsers());
        request.getRequestDispatcher(ADMIN_ORDERS_PAGE_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long userId = Long.valueOf(request.getParameter("userId"));
        User user = userDao.findUser(userId).orElse(null);
        if (user != null) {
            userDao.deleteUser(user);
            request.setAttribute("successMessage", "Successfully user delete");
        } else {
            request.setAttribute("errorMessage", "There was an error");
        }
        doGet(request, response);
    }
}
