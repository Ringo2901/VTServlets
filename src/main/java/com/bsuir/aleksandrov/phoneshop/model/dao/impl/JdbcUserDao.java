package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.UserDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.stock.Stock;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.User;
import com.bsuir.aleksandrov.phoneshop.model.entities.user.UsersExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDao implements UserDao {
    private static volatile UserDao instance;
    private UsersExtractor usersExtractor = new UsersExtractor();
    private static String FIND_USER = "SELECT * FROM users WHERE id = ?";
    private static String FIND_ALL_USERS = "SELECT * FROM users";
    private static String DELETE_USER = "DELETE FROM users WHERE login = ? AND password = ?";
    private static String ADD_USER = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
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
            // LOGGER.log(Level.INFO, "Found {0} phones in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // LOGGER.log(Level.SEVERE, "Error in findProducts", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return user;
    }

    @Override
    public void addUser(User user) {

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
            // LOGGER.log(Level.INFO, "Found {0} phones in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // LOGGER.log(Level.SEVERE, "Error in findProducts", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
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
            // LOGGER.log(Level.INFO, "Found {0} phones in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // LOGGER.log(Level.SEVERE, "Error in findProducts", ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return users;
    }
}
