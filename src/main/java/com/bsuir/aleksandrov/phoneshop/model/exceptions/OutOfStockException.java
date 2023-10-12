package com.bsuir.aleksandrov.phoneshop.model.exceptions;

import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;

public class OutOfStockException extends Exception {
    private Phone phone;
    private int requestedStock;
    private int availableStock;

    public OutOfStockException(Phone phone, int requestedStock, int availableStock) {
        this.phone = phone;
        this.requestedStock = requestedStock;
        this.availableStock = availableStock;
    }

    public OutOfStockException(String s) {
        super(s);
    }

    public Phone getProduct() {
        return phone;
    }

    public int getRequestedStock() {
        return requestedStock;
    }

    public int getAvailableStock() {
        return availableStock;
    }

}
