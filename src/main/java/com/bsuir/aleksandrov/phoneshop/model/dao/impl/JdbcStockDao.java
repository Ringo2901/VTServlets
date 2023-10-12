package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.StockDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.stock.Stock;
import com.bsuir.aleksandrov.phoneshop.model.entities.stock.StocksExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcStockDao implements StockDao {
    private StocksExtractor stocksExtractor = new StocksExtractor();
    private ConnectionPool connectionPool = ConnectionPool.getInstance();
    private static volatile StockDao instance;
    private static final String GET_STOCK_BY_ID = "SELECT * FROM stocks WHERE phoneId = ?";
    private static final String UPDATE_STOCK = "UPDATE stocks SET reserved = ? WHERE phoneId = ?";

    public static StockDao getInstance() {
        if (instance == null) {
            synchronized (StockDao.class) {
                if (instance == null) {
                    instance = new JdbcStockDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Integer availableStock(Long phoneId) {
        Stock stock = getStock(phoneId);
        if (stock != null) {
            return stock.getStock() - stock.getReserved();
        } else {
            return 0;
        }
    }

    @Override
    public void reserve(Long phoneId, int quantity) {
        Stock stock = getStock(phoneId);
        if (stock != null) {
            int newReserved = stock.getReserved() + quantity;
            Connection conn = null;
            PreparedStatement statement = null;
            try {
                conn = connectionPool.getConnection();
                statement = conn.prepareStatement(UPDATE_STOCK);
                statement.setLong(2, phoneId);
                statement.setLong(1, newReserved);
                statement.execute();
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
    }

    private Stock getStock(Long phoneId) {
        Stock stock = null;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_STOCK_BY_ID);
            statement.setLong(1, phoneId);
            ResultSet resultSet = statement.executeQuery();
            stock = stocksExtractor.extractData(resultSet).get(0);
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
        return stock;
    }
}
