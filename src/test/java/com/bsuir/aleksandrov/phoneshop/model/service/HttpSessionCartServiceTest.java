package com.bsuir.aleksandrov.phoneshop.model.service;

import com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.ServiceException;
import com.bsuir.aleksandrov.phoneshop.model.service.impl.HttpSessionCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpSessionCartServiceTest {
    private HttpSessionCartService cartService;
    private HttpServletRequest request;
    private HttpSession session;

    @Before
    public void setup() {
        cartService = HttpSessionCartService.getInstance();
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testGetCart_NewCart() {
        Cart cart = cartService.getCart(request.getSession());
        assertNotNull(cart);
        assertEquals(BigDecimal.ZERO, cart.getTotalCost());
    }

    @Test
    public void testDeleteCartItem() throws OutOfStockException, ServiceException {
        Cart cart = new Cart();
        cartService.add(cart, 1L, 5, request.getSession());

        cartService.delete(cart, 1L, request.getSession());

        assertEquals(0, cart.getTotalItems());
    }

    @Test
    public void testClearCart() throws OutOfStockException, ServiceException {
        Cart cart = new Cart();
        cartService.add(cart, 1L, 5, request.getSession());

        cartService.clear(request.getSession());

        assertEquals(0, cart.getTotalItems());
    }

    @Test
    public void testRemoveCartItem() throws OutOfStockException, ServiceException {
        Cart cart = new Cart();
        cartService.add(cart, 1L, 5, request.getSession());

        cartService.remove(request.getSession(), 1L);

        assertEquals(0, cart.getTotalItems());
    }
}