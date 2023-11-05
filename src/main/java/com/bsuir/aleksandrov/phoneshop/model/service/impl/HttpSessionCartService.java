package com.bsuir.aleksandrov.phoneshop.model.service.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.StockDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcStockDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.cart.Cart;
import com.bsuir.aleksandrov.phoneshop.model.entities.cart.CartItem;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.OutOfStockException;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.ServiceException;
import com.bsuir.aleksandrov.phoneshop.model.service.CartService;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service to work with cart
 *
 * @author nekit
 * @version 1.0
 */
public class HttpSessionCartService implements CartService {
    /**
     * Attribute of cart in session
     */
    private static final String CART_SESSION_ATTRIBUTE = HttpSessionCartService.class.getName() + ".cart";
    /**
     * Attribute of cart in request attribute
     */
    private static final String CART_ATTRIBUTE = "cart";
    /**
     * Instance of HttpSessionCartService
     */
    private static volatile HttpSessionCartService instance;
    /**
     * Instance of PhoneDao
     */
    private PhoneDao phoneDao;
    /**
     * Instance of StockDao
     */
    private StockDao stockDao;

    /**
     * Realisation of Singleton pattern
     *
     * @return instance of HttpSessionCartServiece
     */

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

    /**
     * Constructor of HttpSessionCartService
     */
    private HttpSessionCartService() {
        phoneDao = JdbcPhoneDao.getInstance();
        stockDao = JdbcStockDao.getInstance();
    }

    /**
     * Get cart from session
     *
     * @param currentSession session with cart
     * @return cart from session
     */
    @Override
    public Cart getCart(HttpSession currentSession) {
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

    /**
     * Add Phone to cart
     *
     * @param cart           cart to adding
     * @param productId      productId of phone to add
     * @param quantity       quantity of phone to add
     * @param currentSession session with cart
     * @throws OutOfStockException throws when phone outOfStock
     * @throws ServiceException    throws when there is some errors during service method execution
     */
    @Override
    public void add(Cart cart, Long productId, int quantity, HttpSession currentSession) throws OutOfStockException, ServiceException {
        Optional<CartItem> productMatch;
        synchronized (currentSession) {
            Phone phone;
            try {
                phone = phoneDao.get(productId).orElse(null);
            } catch (DaoException e) {
                throw new ServiceException(e.getMessage());
            }
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

    /**
     * Calculate quantity of phone with cart
     *
     * @param cart  cart with phones to recalculate
     * @param phone phone to recalculate
     * @return available quantity of phone minus quantity of phone in cart
     */
    private int countingQuantityIncludingCart(Cart cart, Phone phone) throws ServiceException {
        int result = 0;
        try {
            result = stockDao.availableStock(phone.getId());
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage());
        }
        Integer quantityInCart = cart.getItems().stream()
                .filter(currProduct -> currProduct.getPhone().equals(phone))
                .map(CartItem::getQuantity)
                .findFirst()
                .orElse(0);
        result -= quantityInCart;
        return result;
    }

    /**
     * Update quantity of phone in cart
     *
     * @param cart           cart to update
     * @param productId      id of phone to update
     * @param quantity       quantity of phone to update
     * @param currentSession session with cart
     * @throws OutOfStockException throws when phone quantity out of stock during updating
     * @throws ServiceException    throws when there is some errors during service method execution
     */
    @Override
    public void update(Cart cart, Long productId, int quantity, HttpSession currentSession) throws OutOfStockException, ServiceException {
        synchronized (currentSession) {
            Phone phone;
            try {
                phone = phoneDao.get(productId).orElse(null);
            } catch (DaoException e) {
                throw new ServiceException(e.getMessage());
            }
            if (phone != null) {
                int availableStock = 0;
                try {
                    availableStock = stockDao.availableStock(phone.getId());
                } catch (DaoException e) {
                    throw new ServiceException(e.getMessage());
                }
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

    /**
     * Delete item from cart
     *
     * @param cart           cart to delete
     * @param productId      id of phone to delete
     * @param currentSession session with cart
     */
    @Override
    public void delete(Cart cart, Long productId, HttpSession currentSession) {
        synchronized (currentSession) {
            cart.getItems().removeIf(item -> productId.equals(item.getPhone().getId()));
            reCalculateCart(cart);
        }
    }

    /**
     * Recalculate cart
     *
     * @param cartToRecalculate cat to recalculate
     */
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

    /**
     * Find cart item in cart
     *
     * @param cart    cart in witch we find
     * @param product product to find
     * @return cartItem
     */
    private Optional<CartItem> getCartItemMatch(Cart cart, Phone product) {
        return cart.getItems().stream().
                filter(currProduct -> currProduct.getPhone().getId().equals(product.getId())).
                findAny();
    }

    /**
     * Clear cart in request
     *
     * @param currentSession session with cart
     */
    @Override
    public void clear(HttpSession currentSession) {
        Cart cart = getCart(currentSession);
        cart.getItems().clear();
        reCalculateCart(cart);
    }

    /**
     * Remove item from cart
     *
     * @param currentSession session with cart
     * @param phoneId        id of phone to remove
     */
    @Override
    public void remove(HttpSession currentSession, Long phoneId) {
        Cart cart = getCart(currentSession);
        cart.getItems().removeIf(item -> phoneId.equals(item.getPhone().getId()));
        reCalculateCart(cart);
    }
}
