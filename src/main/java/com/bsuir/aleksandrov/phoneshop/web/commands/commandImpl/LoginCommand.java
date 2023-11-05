package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.dao.UserDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcUserDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author nekit
 * @version 1.0
 * Command to log in user
 */
public class LoginCommand implements ICommand {
    private UserDao userDao = JdbcUserDao.getInstance();

    /**
     * Return login page jsp path or login user
     *
     * @param request http request
     * @return login page jsp
     * @throws CommandException throws when there is some errors during command execution
     */
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        if (!request.getMethod().equals("GET")) {
            request.setAttribute("messages", login(request, login, hashPassword(password)));
        }
        return JspPageName.AUTHORISATION_JSP;
    }

    /**
     * Check login and password of user and return result of authorisation
     *
     * @param request  http request
     * @param login    login of user
     * @param password password of user
     * @return result of the authorisation
     * @throws CommandException throws when there is some errors during command execution
     */
    private Map<String, String> login(HttpServletRequest request, String login, String password) throws CommandException {
        User user;
        try {
            user = userDao.findUserByLoginAndPass(login, password).orElse(null);
        } catch (DaoException e) {
            throw new CommandException(e.getMessage());
        }
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null) {
            lang = "en";
        }
        Locale locale = new Locale(lang.toString());
        ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
        Map<String, String> messages = new HashMap<>();
        if (user == null) {
            messages.put("error", rb.getString("AUTHORISATION_ERROR"));
        } else {
            request.getSession().setAttribute("role", user.getUserRole().toString());
            request.getSession().setAttribute("login", user.getLogin());
            messages.put("success", rb.getString("AUTHORISATION_SUCCESS"));
        }
        return messages;
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
