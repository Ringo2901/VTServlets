package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserDao {
    Optional<User> findUser(Long id);

    Optional<User> findUserByLoginAndPass(String login, String password);

    Map<String, String> addUser(User user, HttpServletRequest request);

    void deleteUser(User user);

    List<User> findAllUsers();
}
