package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.ColorDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.color.Color;
import com.bsuir.aleksandrov.phoneshop.model.entities.color.ColorsExtractor;
import com.bsuir.aleksandrov.phoneshop.model.exceptions.DaoException;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Using jdbc to work with colors
 *
 * @author nekit
 * @version 1.0
 */
public class JdbcColorDao implements ColorDao {
    /**
     * Field of logger
     */
    private static final Logger log = Logger.getLogger(ColorDao.class);
    /**
     * Extractor of colors
     */
    private final ColorsExtractor colorExtractor = new ColorsExtractor();
    /**
     * Instance of connection pool
     */
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();
    /**
     * SQL query for find colors
     */
    private static final String GET_QUERY = "select COLORS.ID, COLORS.CODE " +
            "from (select * from PHONE2COLOR where PHONEID = ?) p2c " +
            "left join COLORS on p2c.COLORID = COLORS.ID order by COLORS.ID";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Get colors from database
     *
     * @param id - id of phone
     * @return List of colors
     * @throws DaoException throws when there is some errors during dao method execution
     */
    @Override
    public List<Color> getColors(Long id) throws DaoException {
        List<Color> colors;
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            lock.readLock().lock();
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_QUERY);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            colors = colorExtractor.extractData(resultSet);
            log.log(Level.INFO, "Found colors in the database");
        } catch (SQLException ex) {
            log.log(Level.ERROR, "Error in getColors", ex);
            throw new DaoException("Error in process of getting colors");
        } finally {
            lock.readLock().unlock();
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    log.log(Level.ERROR, "Error in closing statement", ex);
                    throw new DaoException("Error in process getting colors");
                }
            }
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return colors;
    }

}
