package com.bsuir.aleksandrov.phoneshop.model.entities.cart;

import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.CloneException;

import java.io.Serializable;

public class CartItem implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private Phone phone;
    private int quantity;

    public CartItem(Phone product, int quantity) {
        this.phone = product;
        this.quantity = quantity;
    }

    public Phone getPhone() {
        return phone;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPhone(Phone product) {
        this.phone = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "code=" + phone.getId() +
                ", quantity=" + quantity;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneException("Error copying the product " + phone.getId() + "with quantity" + quantity);
        }
    }
}
