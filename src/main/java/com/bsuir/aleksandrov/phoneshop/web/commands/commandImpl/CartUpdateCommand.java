package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.ServiceException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.HttpSessionCartService;
import com.bsuir.aleksandrov.phoneshop.web.commands.CommandHelper;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author nekit
 * @version 1.0
 * Command to update cart items
 */
public class CartUpdateCommand implements ICommand {
    private CartService cartService = HttpSessionCartService.getInstance();

    /**
     * Update quantity of cart items, fill errors or success message, return cart jsp path
     *
     * @param request http request
     * @return cart jsp path
     * @throws CommandException throws when there is some errors during command execution
     */
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        Map<Long, String> inputErrors = new HashMap<>();
        String[] productIds = request.getParameterValues("id");
        String[] quantities = request.getParameterValues("quantity");
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null) {
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
            } catch (ServiceException e) {
                throw new CommandException(e.getMessage());
            }
        }
        if (!inputErrors.isEmpty()) {
            request.setAttribute("inputErrors", inputErrors);
        } else {
            request.setAttribute("successMessage", rb.getString("update_success"));
        }
        return CommandHelper.getInstance().getCommand("cart").execute(request);
    }

    /**
     * Return int quantity from string representation
     *
     * @param quantity string representation of quantity
     * @param request  http request
     * @return int representation of quantity
     * @throws ParseException throws when string representation is not a number
     */
    public int parseQuantity(String quantity, HttpServletRequest request) throws ParseException {
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
