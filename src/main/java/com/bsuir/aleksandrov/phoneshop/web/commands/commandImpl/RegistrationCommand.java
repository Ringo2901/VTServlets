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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author nekit
 * @version 1.0
 * Command to register new user
 */
public class RegistrationCommand implements ICommand {
    private UserDao userDao = JdbcUserDao.getInstance();

    /**
     * Return registration page or register new user
     *
     * @param request http request
     * @return registration page jsp path
     * @throws CommandException throws when there is some errors during command execution
     */
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        if (!request.getMethod().equals("GET")) {
            request.setAttribute("message", registration(login, hashPassword(password), request));
        }
        return JspPageName.REGISTRATION_JSP;
    }

    /**
     * Registration of new user
     *
     * @param login    login of user
     * @param password password of user
     * @param request  http request
     * @return result of registration
     * @throws CommandException throws when there is some errors during command execution
     */
    private Map<String, String> registration(String login, String password, HttpServletRequest request) throws CommandException {
        User user = new User(UserRole.USER, login, password);
        try {
            return userDao.addUser(user, request.getSession());
        } catch (DaoException e) {
            throw new CommandException(e.getMessage());
        }
    }

    /**
     * Method to make hash of password using SHA-256
     *
     * @param password user password
     * @return hash of password
     * @throws CommandException throws when we try to use unknown algorithm
     */
    private String hashPassword(String password) throws CommandException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new CommandException(e.getMessage());
        }
        byte[] hashedBytes = md.digest(password.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }
}
