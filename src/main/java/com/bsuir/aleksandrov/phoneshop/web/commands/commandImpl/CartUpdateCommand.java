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

public class CartUpdateCommand implements ICommand {
    private CartService cartService = HttpSessionCartService.getInstance();
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
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
                        cartService.getCart(request.getSession()),
                        Long.parseLong(productIds[i]),
                        parseQuantity(quantities[i], request),
                        request.getSession());
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
            request.getSession().setAttribute("inputErrors", inputErrors);
        } else {
           request.getSession().setAttribute("successMessage", rb.getString("update_success"));
        }
        return request.getHeader("Referer");
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
