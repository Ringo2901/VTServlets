package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.dao.UserDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcUserDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.UserRole;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationAndAuthorisationPageServlet extends HttpServlet {
    UserDao userDao;
    private static final String REGISTRATION_JSP = "/WEB-INF/pages/registrationPage.jsp";
    private static final String AUTHORISATION_JSP = "/WEB-INF/pages/authorisationPage.jsp";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userDao = JdbcUserDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String page = request.getPathInfo().substring(1);
        if (page.equals("registration")) {
            request.getRequestDispatcher(REGISTRATION_JSP).forward(request, response);
        } else {
            if (page.equals("authorisation")) {
                request.getRequestDispatcher(AUTHORISATION_JSP).forward(request, response);
            } else {
                logout(request);
                response.sendRedirect("/products");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operation = request.getParameter("operation");
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        if (operation.equals("registration")) {
            request.setAttribute("messages", registration(login, password));
        } else {
            if (operation.equals("authorisation")) {
                request.setAttribute("messages", login(request, login, password));
            }
        }
        doGet(request, response);
    }

    private Map<String, String> registration(String login, String password) {
        User user = new User(UserRole.User, login, password);
        return userDao.addUser(user);
    }

    private Map<String, String> login(HttpServletRequest request, String login, String password) {
        User user = userDao.findUserByLoginAndPass(login, password).orElse(null);
        Map<String, String> messages = new HashMap<>();
        if (user == null) {
            messages.put("error", "The user does not exist");
        } else {
            request.getSession().setAttribute("role", user.getUserRole().toString());
            request.getSession().setAttribute("login", user.getLogin());
            messages.put("success", "Authorization was successful");
        }
        return messages;
    }

    private void logout(HttpServletRequest request) {
        request.getSession().setAttribute("role", "visitor");
        request.getSession().setAttribute("login", "");
    }
}
