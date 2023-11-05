package com.bsuir.aleksandrov.phoneshop.web;

import com.bsuir.aleksandrov.phoneshop.web.commands.commandImpl.ProductListCommand;
import com.bsuir.aleksandrov.phoneshop.web.exceptions.CommandException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class ProductListCommandTest {

    @Mock
    private HttpServletRequest request;

    private ProductListCommand productListCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        productListCommand = new ProductListCommand();
    }

    @Test
    public void testExecute() throws CommandException {
        String result = productListCommand.execute(request);

        assertEquals(result, "/WEB-INF/pages/productList.jsp");

        verify(request).setAttribute("numberOfPages", 55L);
    }
}
