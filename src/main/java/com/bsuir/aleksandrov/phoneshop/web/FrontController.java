package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.web.commands.CommandHelper;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author nekit
 * @version 1.0
 * Front Controller receives all requests and redirects them for execution to the necessary commands
 */
public class FrontController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /**
     * Name of command parameter
     */
    private static final String COMMAND_NAME = "command";

    /**
     * Simple constructor
     */

    public FrontController() {
        super();
    }

    /**
     * Catch get requests
     *
     * @param request  http request
     * @param response http respouse
     * @throws ServletException
     * @throws IOException
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    /**
     * Catch post requests
     *
     * @param request  http request
     * @param response http response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    /**
     * receives the command name, passes the processing request to the command, sends a response
     *
     * @param request  http request
     * @param response http response
     * @throws ServletException
     * @throws IOException
     */
    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String commandName = request.getParameter(COMMAND_NAME);
        String page;

        if (commandName != null) {
            try {
                ICommand command = CommandHelper.getInstance().getCommand(commandName);
                page = command.execute(request);
            } catch (CommandException e) {
                request.setAttribute("message", e.getMessage());
                page = JspPageName.ERROR_PAGE;
            }
        } else if (request.getParameter("sessionLocale") != null) {
            try {
                page = CommandHelper.getInstance().getCommand("Product_List").execute(request);
            } catch (CommandException e) {
                request.setAttribute("message", e.getMessage());
                page = JspPageName.ERROR_PAGE;
            }
        } else {
            page = JspPageName.ERROR_PAGE;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        if (dispatcher != null) {
            dispatcher.forward(request, response);
        } else {
            errorMessageDireclyFromresponse(response);
        }
    }


    /**
     * Error response when no jsp to send
     *
     * @param response
     * @throws IOException
     */
    private void errorMessageDireclyFromresponse(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println("E R R O R");
    }

}
