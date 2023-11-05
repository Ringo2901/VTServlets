package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.ServiceException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl.CartAddCommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CartAddCommandTest {

    private CartAddCommand cartAddCommand;

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpSession mockSession;
    @Mock
    private CartService mockCartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        cartAddCommand = new CartAddCommand();
        when(mockRequest.getSession()).thenReturn(mockSession);
    }

    @Test
    void execute_AddToCart_Success() throws CommandException, OutOfStockException, ServiceException {
        when(mockRequest.getSession().getAttribute("lang")).thenReturn("en");
        when(mockRequest.getParameter("id")).thenReturn("1");
        when(mockRequest.getParameter("quantity")).thenReturn("3");
        when(mockRequest.getParameter("page_type")).thenReturn("productList");

        when(mockRequest.getLocale()).thenReturn(Locale.US);

        doNothing().when(mockCartService).add(any(), anyLong(), anyInt(), any());

        String result = cartAddCommand.execute(mockRequest);

        assertEquals("/WEB-INF/pages/productList.jsp", result);
        verify(mockRequest).setAttribute(eq("successMessage"), anyString());
    }
}

