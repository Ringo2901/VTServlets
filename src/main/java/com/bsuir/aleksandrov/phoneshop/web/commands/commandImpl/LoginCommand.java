package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.dao.UserDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcUserDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginCommand implements ICommand {
    private UserDao userDao = JdbcUserDao.getInstance();

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
            String login = request.getParameter("login");
            String password = request.getParameter("password");
            if (!request.getMethod().equals("GET")) {
                request.setAttribute("messages", login(request, login, password));
            }
            return JspPageName.AUTHORISATION_JSP;
    }

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
}
