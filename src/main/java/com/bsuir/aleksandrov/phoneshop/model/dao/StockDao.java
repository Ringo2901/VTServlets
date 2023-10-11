package com.bsuir.aleksandrov.phoneshop.model.dao;

public interface StockDao {
    Integer availableStock(Long phoneId);
    void reserve(Long phoneId, int quantity);
}
