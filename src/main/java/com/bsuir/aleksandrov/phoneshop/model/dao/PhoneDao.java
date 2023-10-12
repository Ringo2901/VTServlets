package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortField;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortOrder;

import java.util.List;
import java.util.Optional;

public interface PhoneDao {
    Optional<Phone> get(Long key);

    List<Phone> findAll(int offset, int limit, SortField sortField, SortOrder sortOrder, String query);

    Long numberByQuery(String query);
}
