package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortField;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortOrder;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    private PhoneDao phoneDao;
    private static final String QUERY_PARAMETER = "query";
    private static final String SORT_PARAMETER = "sort";
    private static final String ORDER_PARAMETER = "order";
    private static final String PHONES_ATTRIBUTE = "phones";
    private static final String PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";
    private static final String PAGE_PARAMETER = "page";
    private static final String PAGE_ATTRIBUTE = "numberOfPages";
    private static final int PHONES_ON_PAGE = 10;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        phoneDao = JdbcPhoneDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageNumber = request.getParameter(PAGE_PARAMETER);
        request.setAttribute(PHONES_ATTRIBUTE, phoneDao.findAll(((pageNumber == null ? 1 : Integer.parseInt(pageNumber)) - 1) * PHONES_ON_PAGE, PHONES_ON_PAGE,
                Optional.ofNullable(request.getParameter(SORT_PARAMETER)).map(SortField::valueOf).orElse(null),
                Optional.ofNullable(request.getParameter(ORDER_PARAMETER)).map(SortOrder::valueOf).orElse(null),request.getParameter(QUERY_PARAMETER)));
        Long number = phoneDao.numberByQuery(request.getParameter(QUERY_PARAMETER));
        request.setAttribute(PAGE_ATTRIBUTE, (number + PHONES_ON_PAGE - 1) / PHONES_ON_PAGE);
        request.getRequestDispatcher(PRODUCT_LIST_JSP).forward(request, response);
    }
}
