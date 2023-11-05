package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author nekit
 * @version 1.0
 * Command to log out user
 */
public class LogOutCommand implements ICommand {
    /**
     * Log out user and return authorisation page jsp
     *
     * @param request http request
     * @return authorisation page jsp path
     * @throws CommandException throws when there is some errors during command execution
     */
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        logout(request);
        return JspPageName.AUTHORISATION_JSP;
    }

    /**
     * Remove role and login of user from session
     *
     * @param request http request
     */
    public void logout(HttpServletRequest request) {
        request.getSession().setAttribute("role", "visitor");
        request.getSession().setAttribute("login", "");
    }
}
