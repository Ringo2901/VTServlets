package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl.CartDeleteCommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CartDeleteCommandTest {

    @Test
    public void execute_DeleteCartItem_Success() throws CommandException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpSession mockSession = mock(HttpSession.class);

        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockRequest.getSession().getAttribute("lang")).thenReturn("en");
        when(mockRequest.getParameter("id")).thenReturn("1");

        CartDeleteCommand cartDeleteCommand = new CartDeleteCommand();

        String result = cartDeleteCommand.execute(mockRequest);

        assertEquals("/WEB-INF/pages/cart.jsp", result);
        verify(mockRequest).setAttribute(eq("successMessage"), anyString());
    }

    @Test
    public void execute_DeleteCartItem_WithMissingId_ThrowsException() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        CartDeleteCommand cartDeleteCommand = new CartDeleteCommand();
        HttpSession mockSession = mock(HttpSession.class);

        when(mockRequest.getSession()).thenReturn(mockSession);
        assertThrows(NumberFormatException.class, () -> {
            when(mockRequest.getSession().getAttribute("lang")).thenReturn("en");
            when(mockRequest.getParameter("id")).thenReturn(null);

            cartDeleteCommand.execute(mockRequest);
        });
    }
}
