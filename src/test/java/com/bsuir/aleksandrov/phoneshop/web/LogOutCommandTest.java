package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl.LogOutCommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class LogOutCommandTest {
    private LogOutCommand logOutCommand;
    private HttpServletRequest mockRequest;

    @Before
    public void setUp() {
        logOutCommand = new LogOutCommand();
        mockRequest = mock(HttpServletRequest.class);
        HttpSession mockSession = mock(HttpSession.class);
        when(mockRequest.getSession()).thenReturn(mockSession);
    }

    @Test
    public void testExecute() throws CommandException {
        String result = logOutCommand.execute(mockRequest);
        assertEquals(JspPageName.AUTHORISATION_JSP, result);
    }

    @Test
    public void testLogout() {
        logOutCommand.logout(mockRequest);

        verify(mockRequest.getSession()).setAttribute("role", "visitor");
        verify(mockRequest.getSession()).setAttribute("login", "");
    }
}