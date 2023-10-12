package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.StockDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.UserDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.UsersExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.net.http.HttpRequest;
import java.sql.*;
import java.util.*;

public class JdbcUserDao implements UserDao {
    private static final Logger log = Logger.getLogger(UserDao.class);
    private static volatile UserDao instance;
    private UsersExtractor usersExtractor = new UsersExtractor();
    private static String FIND_USER = "SELECT * FROM users WHERE id = ?";
    private static String FIND_USER_WITH_LOGIN = "SELECT * FROM users WHERE login = ?";
    private static String FIND_USER_WITH_LOGIN_AND_PASSWORD = "SELECT * FROM users WHERE login = ? AND password = ?";
    private static String FIND_ALL_USERS = "SELECT * FROM users WHERE role = 'User'";
    private static String DELETE_USER = "DELETE FROM users WHERE login = ? AND password = ?";
    private static String ADD_USER = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
    private static String MESSAGE_KEY_SUCCESS = "success";
    private static String MESSAGE_KEY_ERROR = "error";
    ConnectionPool connectionPool = ConnectionPool.getInstance();

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

    @Override
    public Map<String, String> addUser(User user, HttpServletRequest request) {
        Connection conn = null;
        PreparedStatement statement = null;
        Map<String, String> messages = new HashMap<>();
        Object lang = request.getSession().getAttribute("lang");
        if (lang == null){
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
