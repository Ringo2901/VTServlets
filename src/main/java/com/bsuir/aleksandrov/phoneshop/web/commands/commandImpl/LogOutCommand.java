package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

public class LogOutCommand implements ICommand {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        logout(request);
        return JspPageName.AUTHORISATION_JSP;
    }

    private void logout(HttpServletRequest request) {
        request.getSession().setAttribute("role", "visitor");
        request.getSession().setAttribute("login", "");
    }
}
