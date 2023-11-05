package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.PhonesExtractor;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortField;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortOrder;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Using jdbc to work with phones
 *
 * @author nekit
 * @version 1.0
 */
public class JdbcPhoneDao implements PhoneDao {
    /**
     * Instance of logger
     */
    private static final Logger log = Logger.getLogger(PhoneDao.class);
    /**
     * Instance of phoneExtractor
     */
    private PhonesExtractor phonesExtractor = new PhonesExtractor();
    /**
     * Instance of PhoneDao
     */
    private static volatile PhoneDao instance;
    /**
     * Instance of ConnectionPool
     */
    private ConnectionPool connectionPool = ConnectionPool.getInstance();
    /**
     * SQL query to find phones by id
     */
    private static final String GET_QUERY = "SELECT * FROM phones WHERE id = ?";
    /**
     * SQL query to find all phones with available stock > 0, limit and offset
     */
    private static final String SIMPLE_FIND_ALL_QUERY = "select ph.* " +
            "from (select PHONES.* from PHONES " +
            "left join STOCKS on PHONES.ID = STOCKS.PHONEID where STOCKS.STOCK - STOCKS.RESERVED > 0 and phones.price > 0 offset ? limit ?) ph";
    /**
     * SQL query to find all phones with available stock
     */
    private static final String FIND_WITHOUT_OFFSET_AND_LIMIT = "SELECT ph.* " +
            "FROM (SELECT phones.* FROM phones " +
            "LEFT JOIN stocks ON phones.id = stocks.phoneId WHERE stocks.stock - stocks.reserved > 0 ";

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * SQL query to find number of phones
     */
    private static final String NUMBER_OF_PHONES_QUERY = "SELECT count(*) FROM PHONES LEFT JOIN STOCKS ON PHONES.ID = STOCKS.PHONEID WHERE STOCKS.STOCK - STOCKS.RESERVED > 0 AND phones.price > 0";

    /**
     * Realisation of Singleton pattern
     *
     * @return instance of PhoneDao
     */
    public static PhoneDao getInstance() {
        if (instance == null) {
            synchronized (PhoneDao.class) {
                if (instance == null) {
                    instance = new JdbcPhoneDao();
                }
            }
        }
        return instance;
    }

    /**
     * Get phone by id from database
     *
     * @param key id of phone
     * @return phone
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public Optional<Phone> get(Long key) throws DaoException {
        Optional<Phone> phone;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            lock.readLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_QUERY);
            statement.setLong(1, key);
            ResultSet resultSet = statement.executeQuery();
            phone = phonesExtractor.extractData(resultSet).stream().findAny();
            log.log(Level.INFO, "Found phones by id in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in get function", ex);
            throw new DaoException("Error in process of getting phone");
        } finally {
            lock.readLock().unlock();
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
        return phone;
    }

    /**
     * Find all phones from database
     *
     * @param offset    - offset of found phones
     * @param limit     - limit of found phones
     * @param sortField - field to sort (model, brand, price, display size)
     * @param sortOrder - sort order (asc or desc)
     * @param query     - query for find
     * @return list of phones
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public List<Phone> findAll(int offset, int limit, SortField sortField, SortOrder sortOrder, String query) throws DaoException {
        List<Phone> phones;
        String sql = makeFindAllSQL(sortField, sortOrder, query);
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            lock.readLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, offset);
            statement.setInt(2, limit);
            ResultSet resultSet = statement.executeQuery();
            phones = phonesExtractor.extractData(resultSet);
            log.log(Level.INFO, "Found all phones in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in findAll", ex);
            throw new DaoException("Error in process of getting all phones");
        } finally {
            lock.readLock().unlock();
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
        return phones;
    }

    /**
     * Find number of phones by query from database
     *
     * @param query - query for find
     * @return number of phones
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public Long numberByQuery(String query) throws DaoException {
        String sql;
        if (query == null || query.equals("")) {
            sql = NUMBER_OF_PHONES_QUERY;
        } else {
            sql = NUMBER_OF_PHONES_QUERY + " AND " +
                    "(LOWER(PHONES.BRAND) LIKE LOWER('" + query + "%') " +
                    "OR LOWER(PHONES.BRAND) LIKE LOWER('% " + query + "%') " +
                    "OR LOWER(PHONES.MODEL) LIKE LOWER('" + query + "%') " +
                    "OR LOWER(PHONES.MODEL) LIKE LOWER('% " + query + "%'))";
        }
        Connection conn = null;
        Statement statement = null;
        try {
            lock.readLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }
            log.log(Level.INFO, "Found count of phones");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in numberByQuery", ex);
            throw new DaoException("Error in process of getting number of phones");
        } finally {
            lock.readLock().unlock();
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
        return 0L;
    }

    /**
     * Make sql query with sorting and finding
     *
     * @param sortField - field to sort
     * @param sortOrder - order to sort
     * @param query     - query to find
     * @return sql query
     */
    private String makeFindAllSQL(SortField sortField, SortOrder sortOrder, String query) {
        if (sortField != null || query != null && !query.equals("")) {
            StringBuilder sql = new StringBuilder(FIND_WITHOUT_OFFSET_AND_LIMIT);

            if (query != null && !query.equals("")) {
                sql.append("AND (" + "LOWER(PHONES.BRAND) LIKE LOWER('").append(query).append("%') ").
                        append("OR LOWER(PHONES.BRAND) LIKE LOWER('% ").append(query).append("%') ").
                        append("OR LOWER(PHONES.MODEL) LIKE LOWER('").append(query).append("%') ").
                        append("OR LOWER(PHONES.MODEL) LIKE LOWER('% ").append(query).append("%')").append(") ");
            }
            sql.append("AND PHONES.PRICE > 0 ");
            if (sortField != null) {
                sql.append("ORDER BY ").append(sortField.name()).append(" ");
                if (sortOrder != null) {
                    sql.append(sortOrder.name()).append(" ");
                } else {
                    sql.append("ASC ");
                }
            }
            sql.append("offset ? limit ?) ph");
            return sql.toString();
        } else {
            return SIMPLE_FIND_ALL_QUERY;
        }
    }
}
