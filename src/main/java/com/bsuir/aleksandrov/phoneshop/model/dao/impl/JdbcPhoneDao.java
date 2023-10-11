package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortField;
import com.bsuir.aleksandrov.phoneshop.model.enums.SortOrder;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.Phone;
import com.bsuir.aleksandrov.phoneshop.model.entities.phone.PhonesExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPhoneDao implements PhoneDao {
    private PhonesExtractor phonesExtractor = new PhonesExtractor();
    private static volatile PhoneDao instance;
    private ConnectionPool connectionPool = ConnectionPool.getInstance();
    private static final String GET_QUERY = "SELECT * FROM phones WHERE id = ?";
    private static final String SIMPLE_FIND_ALL_QUERY = "select ph.* " +
            "from (select PHONES.* from PHONES " +
            "left join STOCKS on PHONES.ID = STOCKS.PHONEID where STOCKS.STOCK - STOCKS.RESERVED > 0 and phones.price > 0 offset ? limit ?) ph";
    private static final String FIND_WITHOUT_OFFSET_AND_LIMIT = "SELECT ph.* " +
            "FROM (SELECT phones.* FROM phones " +
            "LEFT JOIN stocks ON phones.id = stocks.phoneId WHERE stocks.stock - stocks.reserved > 0 ";
    private static final String NUMBER_OF_PHONES_QUERY = "SELECT count(*) FROM PHONES LEFT JOIN STOCKS ON PHONES.ID = STOCKS.PHONEID WHERE STOCKS.STOCK - STOCKS.RESERVED > 0 AND phones.price > 0";

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

    @Override
    public Optional<Phone> get(Long key) {
        Optional<Phone> phone = null;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_QUERY);
            statement.setLong(1, key);
            ResultSet resultSet = statement.executeQuery();
            phone = phonesExtractor.extractData(resultSet).stream().findAny();
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
        return phone;
    }

    @Override
    public List<Phone> findAll(int offset, int limit, SortField sortField, SortOrder sortOrder, String query) {
        List<Phone> phones = new ArrayList<>();
        String sql = makeFindAllSQL(sortField, sortOrder, query);
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, offset);
            statement.setInt(2, limit);
            ResultSet resultSet = statement.executeQuery();
            phones = phonesExtractor.extractData(resultSet);
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
        return phones;
    }

    @Override
    public Long numberByQuery(String query) {
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
            conn = connectionPool.getConnection() ;
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // LOGGER.log(Level.SEVERE, "Error in findProducts", ex);
        }
        finally {
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
        return 0L;
    }

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
