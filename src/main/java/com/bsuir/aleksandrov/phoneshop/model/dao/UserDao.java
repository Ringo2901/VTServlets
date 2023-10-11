package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findUser (Long id);
    void addUser (User user);
    void deleteUser (User user);
    List<User> findAllUsers ();
}
