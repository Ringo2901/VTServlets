package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.color.Color;

import java.util.List;

public interface ColorDao {
    List<Color> getColors(Long id);
}
