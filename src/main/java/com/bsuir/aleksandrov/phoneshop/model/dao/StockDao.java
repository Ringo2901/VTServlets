package com.bsuir.aleksandrov.phoneshop.model.dao;

/**
 * @author nekit
 * @version 1.0
 */
public interface StockDao {
    /**
     * Find available stock of phone
     * @param phoneId id of phone
     * @return available stock
     */
    Integer availableStock(Long phoneId);

    /**
     * Update reserve of phones in database
     * @param phoneId - phone to update
     * @param quantity - quantity to add in reserve field
     */
    void reserve(Long phoneId, int quantity);
}
