package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.HttpSessionCartService;
import com.bsuir.aleksandrov.phoneshop.web.commands.CommandHelper;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author nekit
 * @version 1.0
 * Command to delete cart item
 */
public class CartDeleteCommand implements ICommand {
    private CartService cartService = HttpSessionCartService.getInstance();

    /**
     * Delete cart item and return cart jsp
     *
     * @param request http request
     * @return cart jsp
     * @throws CommandException throws when there is some errors during command execution
     */
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null) {
            lang = "en";
        }
        Locale locale = new Locale(lang.toString());
        ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
        int phoneId = Integer.parseInt(request.getParameter("id"));
        cartService.delete(cartService.getCart(request.getSession()), (long) phoneId, request.getSession());
        request.setAttribute("successMessage", rb.getString("delete_success"));
        return CommandHelper.getInstance().getCommand("cart").execute(request);
    }
}
