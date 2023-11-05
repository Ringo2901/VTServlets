package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.ServiceException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.HttpSessionCartService;
import com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl.CartUpdateCommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CartUpdateCommandTest {

    private CartUpdateCommand cartUpdateCommand;
    private CartService cartService = HttpSessionCartService.getInstance();

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cartUpdateCommand = new CartUpdateCommand();
        HttpSession mockSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(mockSession);
    }

    @Test
    public void testExecute_withValidInput() throws CommandException {
        // Arrange
        when(request.getParameterValues("id")).thenReturn(new String[]{"1", "2"});
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"3", "4"});
        when(request.getSession().getAttribute("lang")).thenReturn("en");
        ResourceBundle rb = ResourceBundle.getBundle("messages", new Locale("en"));
        when(request.getLocale()).thenReturn(new Locale("en"));

        String result = cartUpdateCommand.execute(request);

        assertEquals("/WEB-INF/pages/cart.jsp", result); // Replace with actual path
        verify(request, times(1)).setAttribute(eq("successMessage"), anyString());
        verify(request, times(0)).setAttribute(eq("inputErrors"), anyMap());
    }

    @Test
    public void testExecute_withOutOfStockException() throws CommandException {
        // Arrange
        when(request.getParameterValues("id")).thenReturn(new String[]{"1"});
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"10"});
        when(request.getSession().getAttribute("lang")).thenReturn("en");
        ResourceBundle rb = ResourceBundle.getBundle("messages", new Locale("en"));
        when(request.getLocale()).thenReturn(new Locale("en"));
        // Act
        String result = cartUpdateCommand.execute(request);

        // Assert
        assertEquals("/WEB-INF/pages/cart.jsp", result);
    }

    @Test
    public void testParseQuantity_withValidInput() throws ParseException {
        // Arrange
        when(request.getLocale()).thenReturn(new java.util.Locale("en"));

        // Act
        int result = cartUpdateCommand.parseQuantity("3", request);

        // Assert
        assertEquals(3, result);
    }

    @Test(expected = ParseException.class)
    public void testParseQuantity_withInvalidInput() throws ParseException {
        // Arrange
        when(request.getLocale()).thenReturn(new java.util.Locale("en"));

        // Act
        cartUpdateCommand.parseQuantity("abc", request);
    }
}
