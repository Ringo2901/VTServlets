package com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortField;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortOrder;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.web.JspPageName;
import com.bsuir.aleksandrov.phoneshop.web.commands.ICommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * @author nekit
 * @version 1.0
 * Command to show list of products
 */
public class ProductListCommand implements ICommand {
    private final PhoneDao phoneDao = JdbcPhoneDao.getInstance();
    private static final String QUERY_PARAMETER = "query";
    private static final String SORT_PARAMETER = "sort";
    private static final String ORDER_PARAMETER = "order";
    private static final String PHONES_ATTRIBUTE = "phones";
    private static final String PAGE_PARAMETER = "page";
    private static final String PAGE_ATTRIBUTE = "numberOfPages";
    private static final int PHONES_ON_PAGE = 10;

    /**
     * Get product list and return product list jsp
     * @param request http request
     * @return product list jsp path
     * @throws CommandException throws when there is some errors during command execution
     */
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        String pageNumber = request.getParameter(PAGE_PARAMETER);
        Long number;
        try {
            request.setAttribute(PHONES_ATTRIBUTE, phoneDao.findAll(((pageNumber == null ? 1 : Integer.parseInt(pageNumber)) - 1) * PHONES_ON_PAGE, PHONES_ON_PAGE,
                    Optional.ofNullable(request.getParameter(SORT_PARAMETER)).map(SortField::valueOf).orElse(null),
                    Optional.ofNullable(request.getParameter(ORDER_PARAMETER)).map(SortOrder::valueOf).orElse(null), request.getParameter(QUERY_PARAMETER)));
            number = phoneDao.numberByQuery(request.getParameter(QUERY_PARAMETER));
        } catch (DaoException e) {
            throw new CommandException(e.getMessage());
        }
        request.setAttribute(PAGE_ATTRIBUTE, (number + PHONES_ON_PAGE - 1) / PHONES_ON_PAGE);
        return JspPageName.PRODUCT_LIST_JSP;
    }
}
