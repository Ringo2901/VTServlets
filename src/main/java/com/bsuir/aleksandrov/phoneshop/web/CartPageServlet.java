package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.HttpSessionCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class CartPageServlet extends HttpServlet {
    private CartService cartService;
    private static final String CART_ATTRIBUTE = "cart";
    private static final String CART_JSP = "/WEB-INF/pages/cart.jsp";
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = HttpSessionCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().setAttribute("inputErrors", null);
        request.setAttribute(CART_ATTRIBUTE, cartService.getCart(request));
        request.getRequestDispatcher(CART_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("addOperation") != null) {
            addItem(request, response);
        } else if (request.getParameter("deleteOperation") != null) {
            deleteItem(request, response);
        } else if (request.getParameter("updateOperation") != null) {
            updateItems(request, response);
        }
    }

    private void addItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<Long, String> inputErrors = new HashMap<>();
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null){
            lang = "en";
        }
        Locale locale = new Locale(lang.toString());
        ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
        int phoneId = Integer.parseInt(request.getParameter("id"));
        try {
            int quantity = parseQuantity(request.getParameter("quantity"), request);
            cartService.add(cartService.getCart(request), (long) phoneId, quantity, request);
        } catch (OutOfStockException e) {
            inputErrors.put((long) phoneId, rb.getString("NOT_ENOUGH_ERROR") + e.getAvailableStock());
        } catch (ParseException e) {
            inputErrors.put((long) phoneId, rb.getString("NOT_A_NUMBER_ERROR"));
        }
        request.getSession().setAttribute("inputErrors", inputErrors);
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contentEquals(request.getRequestURL())) {
            if (inputErrors.isEmpty() ||  !referer.contains("message")) {
                response.sendRedirect(referer + (!referer.contains("?")? "?message=Phone was successfully added!": "&message=Phone was successfully added!"));
            } else {
                response.sendRedirect(referer.substring(0, referer.indexOf("message")));
            }
        } else {
            if (inputErrors.isEmpty() || !referer.contains("message")) {
                response.sendRedirect("/products" + (!referer.contains("?")? "?message=Phone was successfully added!": "&message=Phone was successfully added!"));
            } else {
                response.sendRedirect("/products");
            }
        }
    }

    private void updateItems(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Map<Long, String> inputErrors = new HashMap<>();
        String[] productIds = request.getParameterValues("id");
        String[] quantities = request.getParameterValues("quantity");
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null){
            lang = "en";
        }
        Locale locale = new Locale(lang.toString());
        ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
        for (int i = 0; i < productIds.length; i++) {
            try {
                cartService.update(
                        cartService.getCart(request),
                        Long.parseLong(productIds[i]),
                        parseQuantity(quantities[i], request),
                        request);
            } catch (OutOfStockException e) {
                inputErrors.put(
                        Long.parseLong(productIds[i]),
                        rb.getString("NOT_ENOUGH_ERROR") + e.getAvailableStock());
            } catch (NumberFormatException | ParseException e1) {
                inputErrors.put(
                        Long.parseLong(productIds[i]),
                        rb.getString("NOT_A_NUMBER_ERROR"));
            }
        }
        if (!inputErrors.isEmpty()) {
            request.setAttribute("inputErrors", inputErrors);
        } else {
            response.sendRedirect(String.format("%s/cart?message=Cart was successfully updated!", request.getContextPath()));
        }
        doGet(request, response);
    }

    private void deleteItem(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int phoneId = Integer.parseInt(request.getParameter("id"));
        cartService.delete(cartService.getCart(request), (long) phoneId, request);
        response.sendRedirect(String.format("%s/cart?message=Item successfully deleted!", request.getContextPath()));
        doGet(request, response);
    }

    private int parseQuantity(String quantity, HttpServletRequest request) throws ParseException {
        int result;
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null){
            lang = "en";
        }
        Locale locale = new Locale(lang.toString());
        ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
        if (!quantity.matches("^\\d+([\\.\\,]\\d+)?$")) {
            throw new ParseException(rb.getString("NOT_A_NUMBER_ERROR"), 0);
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance(request.getLocale());
        result = numberFormat.parse(quantity).intValue();

        return result;
    }
}