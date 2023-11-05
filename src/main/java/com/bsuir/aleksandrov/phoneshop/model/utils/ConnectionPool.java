package com.bsuir.aleksandrov.phoneshop.model.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Connection pool to work with database
 * @author nekit
 * @version 1.0
 */
public class ConnectionPool {
    /**
     * Instance of connection pool
     */
    private static ConnectionPool instance = null;
    /**
     * url of database
     */
    private final String url;
    /**
     * user of database
     */
    private final String user;
    /**
     * password to database
     */
    private final String password;
    /**
     * max connection to database
     */
    private final int maxConnections = 10;
    /**
     * List of connections
     */
    private final List<Connection> connectionPool;
    /**
     * List of connection in use
     */
    private final List<Connection> usedConnections = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Realisation of Singleton pattern
     * @return instance of Connection pool
     */
    public synchronized static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }

    /**
     * Constructor of Connection pool that get url, user and password from properties file
     */
    public ConnectionPool() {
        ResourceBundle bundle = ResourceBundle.getBundle("database");
        url = bundle.getString("db.url") + bundle.getString("db.name");
        user = bundle.getString("db.user");
        password = bundle.getString("db.password");
        this.connectionPool = new ArrayList<>(maxConnections);
    }

    /**
     * Get connection to database
     * @return instance of connection
     * @throws SQLException throws when fail to connect to database
     */
    public synchronized Connection getConnection() throws SQLException {
        lock.readLock().lock();
        try {
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
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Remove connection from used connections
     * @param connection connection to release
     */
    public synchronized void releaseConnection(Connection connection) {
        lock.writeLock().lock();
        try {
            usedConnections.remove(connection);
            connectionPool.add(connection);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Create connection
     * @return connection
     * @throws SQLException when connection fail
     */
    private Connection createConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        return DriverManager.getConnection(url, properties);
    }

    /**
     * Close all connections
     * @throws SQLException throws when fail to close
     */
    public void closeAllConnections() throws SQLException {
        lock.writeLock().lock();
        try {
            for (Connection connection : connectionPool) {
                connection.close();
            }
            connectionPool.clear();
            for (Connection connection : usedConnections) {
                connection.close();
            }
            usedConnections.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
