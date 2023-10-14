package com.bsuir.aleksandrov.phoneshop.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Support localization
 * @author nekit
 * @version 1.0
 */
public class LocalizationFilter implements Filter {
    /**
     * Filter, that provide placing localization on session
     * @param request request with session
     * @param response response
     * @param chain chain of filters
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)

            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        if (req.getParameter("sessionLocale") != null) {

            req.getSession().setAttribute("lang", req.getParameter("sessionLocale"));

        }

        chain.doFilter(request, response);

    }
}