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

public class FrontController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String COMMAND_NAME = "command";

    public FrontController() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String commandName = request.getParameter(COMMAND_NAME);
        if (commandName != null) {
            ICommand command = CommandHelper.getInstance().getCommand(commandName);
            String page;
            try {
                page = command.execute(request);
            } catch (CommandException e) {
                page = JspPageName.ERROR_PAGE;
            } catch (Exception e) {
                page = JspPageName.ERROR_PAGE;
            }
            if (request.getMethod().equals("GET")) {
                RequestDispatcher dispatcher = request.getRequestDispatcher(page);
                if (dispatcher != null) {
                    dispatcher.forward(request, response);
                } else {
                    errorMessageDireclyFromresponse(response);
                }
            } else {
                response.sendRedirect(page);
            }
        } else {
            if (request.getParameter("sessionLocale")!=null){
                response.sendRedirect(request.getHeader("Referer"));
            }
        }
    }

    private void errorMessageDireclyFromresponse(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println("E R R O R");
    }

}
