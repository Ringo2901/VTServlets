package com.bsuir.aleksandrov.phoneshop.model.dao;

import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
     *
     * @param id id of user
     * @return user
     * @throws DaoException throws when there is some errors during dao method execution
     */
    Optional<User> findUser(Long id) throws DaoException;

    /**
     * Find user by login and password
     *
     * @param login    login of user
     * @param password password of user
     * @return user
     * @throws DaoException throws when there is some errors during dao method execution
     */
    Optional<User> findUserByLoginAndPass(String login, String password) throws DaoException;

    /**
     * Add new user to database
     *
     * @param user    user to add
     * @param session session of adding
     * @return map of errors
     * @throws DaoException throws when there is some errors during dao method execution
     */
    Map<String, String> addUser(User user, HttpSession session) throws DaoException;

    /**
     * Delete user from database
     *
     * @param user user to delete
     * @throws DaoException throws when there is some errors during dao method execution
     */
    void deleteUser(User user) throws DaoException;

    /**
     * Find all users in database
     *
     * @return List of users
     * @throws DaoException throws when there is some errors during dao method execution
     */
    List<User> findAllUsers() throws DaoException;
}
