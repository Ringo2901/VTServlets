package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.dao.UserDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcUserDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.UserRole;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public class RegistrationCommand implements ICommand {
    private UserDao userDao = JdbcUserDao.getInstance();
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!request.getMethod().equals("GET")) {
            String login = request.getParameter("login");
            String password = request.getParameter("password");
            request.setAttribute("message", registration(login, password, request));
            return request.getHeader("Referer");
        } else {
            return JspPageName.REGISTRATION_JSP;
        }
    }

    private Map<String, String> registration(String login, String password, HttpServletRequest request) throws CommandException {
        User user = new User(UserRole.User, login, password);
        try {
            return userDao.addUser(user, request);
        } catch (DaoException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
