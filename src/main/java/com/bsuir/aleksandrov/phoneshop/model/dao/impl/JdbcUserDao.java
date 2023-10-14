package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.UserDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.UsersExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

/**
 * Using jdbc to work with users
 * @author nekit
 * @version 1.0
 */
public class JdbcUserDao implements UserDao {
    /**
     * Instance of logger
     */
    private static final Logger log = Logger.getLogger(UserDao.class);
    /**
     * Instance of UserDao
     */
    private static volatile UserDao instance;
    /**
     * UsersExtractor
     */
    private UsersExtractor usersExtractor = new UsersExtractor();
    /**
     * SQL query to find user by id
     */
    private static String FIND_USER = "SELECT * FROM users WHERE id = ?";
    /**
     * SQL query to find user by login
     */
    private static String FIND_USER_WITH_LOGIN = "SELECT * FROM users WHERE login = ?";
    /**
     * SQL query to find users by login and password
     */
    private static String FIND_USER_WITH_LOGIN_AND_PASSWORD = "SELECT * FROM users WHERE login = ? AND password = ?";
    /**
     * SQL query to find all users with role User
     */
    private static String FIND_ALL_USERS = "SELECT * FROM users WHERE role = 'User'";
    /**
     * SQL query to delete user with login and password
     */
    private static String DELETE_USER = "DELETE FROM users WHERE login = ? AND password = ?";
    /**
     * SQL query to insert new user
     */
    private static String ADD_USER = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
    /**
     * Key to map when success
     */
    private static String MESSAGE_KEY_SUCCESS = "success";
    /**
     * Key to map when error
     */
    private static String MESSAGE_KEY_ERROR = "error";
    /**
     * Instance of connection pool
     */
    private ConnectionPool connectionPool = ConnectionPool.getInstance();

    /**
     * Realisation of Singleton pattern
     * @return instance of UserDao
     */
    public static UserDao getInstance() {
        if (instance == null) {
            synchronized (UserDao.class) {
                if (instance == null) {
                    instance = new JdbcUserDao();
                }
            }
        }
        return instance;
    }

    /**
     * Find user by id
     * @param id id of user
     * @return user
     */
    @Override
    public Optional<User> findUser(Long id) {
        Optional<User> user = Optional.empty();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(FIND_USER);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            user = usersExtractor.extractData(resultSet).stream().findAny();
            log.log(Level.INFO, "Found user by id in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            log.log(Level.ERROR, "Error in findUser", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "Error in findUser", ex);
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return user;
    }

    /**
     * Find user by login and password
     * @param login login of user
     * @param password password of user
     * @return user
     */
    @Override
    public Optional<User> findUserByLoginAndPass(String login, String password) {
        Optional<User> user = Optional.empty();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(FIND_USER_WITH_LOGIN_AND_PASSWORD);
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            user = usersExtractor.extractData(resultSet).stream().findAny();
            log.log(Level.INFO, "Found user by login and pass in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in findUserByLoginAndPass", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "Error in closing statement", ex);
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return user;
    }

    /**
     * Add new user
     * @param user user to add
     * @param request request of adding
     * @return Map with errors or success messages
     */
    @Override
    public Map<String, String> addUser(User user, HttpServletRequest request) {
        Connection conn = null;
        PreparedStatement statement = null;
        Map<String, String> messages = new HashMap<>();
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null) {
            lang = "en";
        }
        Locale locale = new Locale(lang.toString());
        ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(FIND_USER_WITH_LOGIN);
            statement.setString(1, user.getLogin());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                statement = conn.prepareStatement(ADD_USER);
                statement.setString(1, user.getLogin());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getUserRole().toString());
                statement.executeUpdate();
                messages.put(MESSAGE_KEY_SUCCESS, rb.getString("REGISTRATION_SUCCESS"));
            } else {
                messages.put(MESSAGE_KEY_ERROR, rb.getString("REGISTRATION_ERROR"));
            }
            log.log(Level.INFO, "Add user");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in addUser", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "Error in closing statement", ex);
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return messages;
    }

    /**
     * Delete user from database
     * @param user user to delete
     */
    @Override
    public void deleteUser(User user) {
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(DELETE_USER);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
            log.log(Level.INFO, "Delete user");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in deleteUser", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "Error in closing statement", ex);
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    /**
     * Find all users in database
     * @return List of users
     */
    @Override
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        Statement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(FIND_ALL_USERS);
            users = usersExtractor.extractData(resultSet);
            log.log(Level.INFO, "Found all users in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in findAllUsers", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "Error in closing statement", ex);
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return users;
    }
}
