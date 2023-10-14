package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.ColorDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.color.Color;
import com.bsuir.aleksandrov.phoneshop.model.entities.color.ColorsExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Using jdbc to work with colors
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

    /**
     * Get colors from database
     * @param id - id of phone
     * @return List of colors
     */
    @Override
    public List<Color> getColors(Long id) {
        List<Color> colors = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = connectionPool.getConnection();
            statement = conn.prepareStatement(GET_QUERY);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            colors = colorExtractor.extractData(resultSet);
            log.log(Level.INFO, "Found colors in the database");
        } catch (SQLException ex) {
            ex.printStackTrace();
             log.log(Level.ERROR, "Error in getColors", ex);
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
        return colors;
    }

}
