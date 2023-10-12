package com.bsuir.aleksandrov.phoneshop.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationFilter implements Filter {
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