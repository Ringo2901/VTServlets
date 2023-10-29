package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

public class ProductDetailsCommand implements ICommand {
    private PhoneDao phoneDao = JdbcPhoneDao.getInstance();
    private static final String PHONE_ATTRIBUTE = "phone";
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        Phone phone = phoneDao.get(Long.valueOf(request.getParameter("phone_id"))).orElse(null);
        if (phone != null) {
            request.setAttribute(PHONE_ATTRIBUTE, phone);
            return JspPageName.PRODUCT_PAGE;
        }
        else{
            return JspPageName.PRODUCT_NOT_FOUND_PAGE;
        }
    }
}
