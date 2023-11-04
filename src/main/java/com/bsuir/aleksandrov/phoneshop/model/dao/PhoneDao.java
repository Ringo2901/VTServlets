package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortField;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortOrder;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;

import java.util.List;
import java.util.Optional;

/**
 * @author nekit
 * @version 1.0
 */
public interface PhoneDao {
    /**
     * Find phone by id
     * @param key id of phone
     * @return phone with id
     */
    Optional<Phone> get(Long key) throws DaoException;

    /**
     * Find phones from database
     * @param offset - offset of found phones
     * @param limit - limit of found phones
     * @param sortField - field to sort (model, brand, price, display size)
     * @param sortOrder - sort order (asc or desc)
     * @param query - query for find
     * @return List of phones
     */

    List<Phone> findAll(int offset, int limit, SortField sortField, SortOrder sortOrder, String query) throws DaoException;

    /**
     * Number of founded phones
     * @param query - query for find
     * @return number of phones
     */
    Long numberByQuery(String query) throws DaoException;
}
