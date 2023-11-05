package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.color.Color;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;

import java.util.List;

/**
 * @author nekit
 * @version 1.0
 */
public interface ColorDao {
    /**
     * Extract Colors from dataBase
     *
     * @param id - id of phone
     * @return List of colors
     * @throws DaoException throws when there is some errors during dao method execution
     */
    List<Color> getColors(Long id) throws DaoException;
}
