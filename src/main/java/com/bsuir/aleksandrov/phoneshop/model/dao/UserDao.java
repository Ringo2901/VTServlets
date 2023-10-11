package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;

import java.util.List;

public interface UserDao {
    User findUser (String login, String password);
    void addUser (User user);
    void deleteUser (User user);
    List<User> findAllUsers ();
}
