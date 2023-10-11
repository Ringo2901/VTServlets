package com.bsuir.aleksandrov.phoneshop.model.utils;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConnectionPool {
    private static ConnectionPool instance = null;
    private final String url;
    private final String user;
    private final String password;
    private final int maxConnections = 10;
    private final List<Connection> connectionPool;
    private final List<Connection> usedConnections = new ArrayList<>();
    public synchronized static ConnectionPool getInstance(){
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }
    public ConnectionPool() {
        ResourceBundle bundle = ResourceBundle.getBundle("database");
        url = bundle.getString("db.url") + bundle.getString("db.name");
        user= bundle.getString("db.user");
        password = bundle.getString("db.password");
        this.connectionPool = new ArrayList<>(maxConnections);
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < maxConnections) {
                Connection connection = createConnection();
                usedConnections.add(connection);
                return connection;
            } else {
                throw new SQLException("Reached maximum connections limit.");
            }
        } else {
            Connection connection = connectionPool.remove(connectionPool.size() - 1);
            usedConnections.add(connection);
            return connection;
        }
    }

    public synchronized void releaseConnection(Connection connection) {
        usedConnections.remove(connection);
        connectionPool.add(connection);
    }

    private Connection createConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        return DriverManager.getConnection(url, properties);
    }

    public void closeAllConnections() throws SQLException {
        for (Connection connection : connectionPool) {
            connection.close();
        }
        connectionPool.clear();
        for (Connection connection : usedConnections) {
            connection.close();
        }
        usedConnections.clear();
    }
}
