package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author nekit
 * @version 1.0
 * Command to get product details page
 */
public class ProductDetailsCommand implements ICommand {
    private final PhoneDao phoneDao = JdbcPhoneDao.getInstance();
    private static final String PHONE_ATTRIBUTE = "phone";

    /**
     * Return product details page of current phone
     * @param request http request
     * @return product details page jsp path
     * @throws CommandException throws when there is some errors during command execution
     */
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        Phone phone;
        try {
            if (request.getParameter("phone_id") == null){
                phone = phoneDao.get(Long.parseLong(request.getAttribute("phone_id").toString())).orElse(null);
            } else {
                phone = phoneDao.get(Long.valueOf(request.getParameter("phone_id"))).orElse(null);
            }
        } catch (DaoException e) {
            throw new CommandException(e.getMessage());
        }
        if (phone != null) {
            request.setAttribute(PHONE_ATTRIBUTE, phone);
            return JspPageName.PRODUCT_PAGE;
        }
        else{
            return JspPageName.PRODUCT_NOT_FOUND_PAGE;
        }
    }
}
