package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author nekit
 * @version 1.0
 */
public interface UserDao {
    /**
     * Find user by id
     * @param id id of user
     * @return user
     */
    Optional<User> findUser(Long id);

    /**
     * Find user by login and password
     * @param login login of user
     * @param password password of user
     * @return user
     */
    Optional<User> findUserByLoginAndPass(String login, String password);

    /**
     * Add new user to database
     * @param user user to add
     * @param request request of adding
     * @return map of errors
     */
    Map<String, String> addUser(User user, HttpServletRequest request);

    /**
     * Delete user from database
     * @param user user to delete
     */
    void deleteUser(User user);

    /**
     * Find all users in database
     * @return List of users
     */
    List<User> findAllUsers();
}
