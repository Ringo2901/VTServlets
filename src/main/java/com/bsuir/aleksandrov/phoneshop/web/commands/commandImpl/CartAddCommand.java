package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.HttpSessionCartService;
import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class CartAddCommand implements ICommand {
    private CartService cartService = HttpSessionCartService.getInstance();

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        Map<Long, String> inputErrors = new HashMap<>();
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null) {
            lang = "en";
        }
        Locale locale = new Locale(lang.toString());
        ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
        int phoneId = Integer.parseInt(request.getParameter("id"));
        try {
            int quantity = parseQuantity(request.getParameter("quantity"), request);
            cartService.add(cartService.getCart(request.getSession()), (long) phoneId, quantity, request.getSession());
        } catch (OutOfStockException e) {
            inputErrors.put((long) phoneId, rb.getString("NOT_ENOUGH_ERROR") + e.getAvailableStock());
        } catch (ParseException e) {
            inputErrors.put((long) phoneId, rb.getString("NOT_A_NUMBER_ERROR"));
        }
        String referer = request.getHeader("Referer");
        if (inputErrors.isEmpty()) {
            if (referer.contains("successMessage")){
                return referer;
            } else{
                return referer+"&successMessage="+rb.getString("add_success");
            }
            //request.getSession().setAttribute("successMessage", rb.getString("add_success"));
        } else {
            if (referer.contains("inputErrors")){
                return referer;
            } else {
                return referer + "&inputErrors="+inputErrors.values();
            }
            //request.getSession().setAttribute("inputErrors", inputErrors);
        }
    }

    private int parseQuantity(String quantity, HttpServletRequest request) throws ParseException {
        int result;
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null) {
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
