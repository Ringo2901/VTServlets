package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ProductDetailsPageServlet extends HttpServlet {
    private PhoneDao productDao;
    private static final String PRODUCT_PAGE = "/WEB-INF/pages/productPage.jsp";
    private static final String PRODUCT_NOT_FOUND_PAGE = "/WEB-INF/pages/productNotFoundPage.jsp";
    private static final String PHONE_ATTRIBUTE = "phone";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = JdbcPhoneDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Phone phone = productDao.get(Long.parseLong(request.getPathInfo().substring(1))).orElse(null);
        if (phone != null) {
            request.setAttribute(PHONE_ATTRIBUTE, phone);
            request.getRequestDispatcher(PRODUCT_PAGE).forward(request, response);
        }
        else{
            request.getRequestDispatcher(PRODUCT_NOT_FOUND_PAGE).forward(request, response);
        }
    }
}
