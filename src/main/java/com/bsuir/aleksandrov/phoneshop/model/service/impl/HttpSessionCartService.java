package com.bsuir.aleksandrov.phoneshop.model.service.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.StockDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcStockDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart;
import com.bsuir.aleksandrov.phoneshop.model.entities.cart.CartItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Optional;

public class HttpSessionCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = HttpSessionCartService.class.getName() + ".cart";
    private static final String CART_ATTRIBUTE = "cart";
    private static volatile HttpSessionCartService instance;
    private static final String ERROR_MESSAGE = "No such product with given code";
    private PhoneDao phoneDao;
    private StockDao stockDao;

    public static HttpSessionCartService getInstance() {
        if (instance == null) {
            synchronized (HttpSessionCartService.class) {
                if (instance == null) {
                    instance = new HttpSessionCartService();
                }
            }
        }
        return instance;
    }

    private HttpSessionCartService() {
        phoneDao = JdbcPhoneDao.getInstance();
        stockDao = JdbcStockDao.getInstance();
    }

    @Override
    public Cart getCart(HttpServletRequest request) {
        HttpSession currentSession = request.getSession();
        synchronized (currentSession) {
            Cart cart = (Cart) currentSession.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
                currentSession.setAttribute(CART_SESSION_ATTRIBUTE, cart);
            }
            if (cart.getTotalCost() == null) {
                cart.setTotalCost(BigDecimal.ZERO);
            }
            return cart;
        }
    }

    @Override
    public void add(Cart cart, Long productId, int quantity, HttpServletRequest request) throws OutOfStockException {
        HttpSession currentSession = request.getSession();
        Optional<CartItem> productMatch;
        synchronized (currentSession) {
            Phone phone = phoneDao.get(productId).orElse(null);
            if (phone != null) {
                if (countingQuantityIncludingCart(cart, phone) < quantity) {
                    throw new OutOfStockException(phone, quantity, countingQuantityIncludingCart(cart, phone));
                }
                if ((productMatch = getCartItemMatch(cart, phone)).isPresent()) {
                    cart.getItems().
                            get(cart.getItems().indexOf(productMatch.get())).
                            setQuantity(productMatch.get().getQuantity() + quantity);
                } else {
                    cart.getItems().add(new CartItem(phone, quantity));
                    currentSession.setAttribute(CART_ATTRIBUTE, cart);
                }
                reCalculateCart(cart);
            }
        }
    }

    private int countingQuantityIncludingCart(Cart cart, Phone phone) {
        int result = stockDao.availableStock(phone.getId());
        Integer quantityInCart = cart.getItems().stream()
                .filter(currProduct -> currProduct.getPhone().equals(phone))
                .map(CartItem::getQuantity)
                .findFirst()
                .orElse(0);
        result -= quantityInCart;
        return result;
    }

    @Override
    public void update(Cart cart, Long productId, int quantity, HttpServletRequest request) throws OutOfStockException {
        HttpSession currentSession = request.getSession();
        synchronized (currentSession) {
            Phone phone = phoneDao.get(productId).orElse(null);
            if (phone != null) {
                int availableStock = stockDao.availableStock(phone.getId());
                if (quantity > availableStock) {
                    throw new OutOfStockException(phone, quantity, availableStock);
                }
                getCartItemMatch(cart, phone).ifPresent(cartItem -> cart.getItems().
                        get(cart.getItems().indexOf(cartItem)).
                        setQuantity(quantity));
                reCalculateCart(cart);
            }
        }
    }

    @Override
    public void delete(Cart cart, Long productId, HttpServletRequest request) {
        HttpSession currentSession = request.getSession();
        synchronized (currentSession) {
            cart.getItems().removeIf(item -> productId.equals(item.getPhone().getId()));
            reCalculateCart(cart);
        }
    }

    @Override
    public void reCalculateCart(Cart cartToRecalculate) {
        BigDecimal totalCost = BigDecimal.ZERO;
        cartToRecalculate.setTotalItems(
                cartToRecalculate.getItems().stream().
                        map(CartItem::getQuantity).
                        mapToInt(q -> q).
                        sum()
        );
        for (CartItem item : cartToRecalculate.getItems()) {
            totalCost = totalCost.add(
                    item.getPhone().getPrice().
                            multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        cartToRecalculate.setTotalCost(totalCost);
    }

    private Optional<CartItem> getCartItemMatch(Cart cart, Phone product) {
        return cart.getItems().stream().
                filter(currProduct -> currProduct.getPhone().getId().equals(product.getId())).
                findAny();
    }

    @Override
    public void clear(HttpServletRequest request) {
        Cart cart = getCart(request);
        cart.getItems().clear();
        reCalculateCart(cart);
    }

    @Override
    public void remove(HttpServletRequest request, Long phoneId) {
        Cart cart = getCart(request);
        cart.getItems().removeIf(item -> phoneId.equals(item.getPhone().getId()));
        reCalculateCart(cart);
    }
}
