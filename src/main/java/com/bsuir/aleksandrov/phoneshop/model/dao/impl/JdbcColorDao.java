package com.bsuir.aleksandrov.phoneshop.model.dao.impl;

import com.bsuir.aleksandrov.phoneshop.model.dao.ColorDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.color.Color;
import com.bsuir.aleksandrov.phoneshop.model.entities.color.ColorsExtractor;
import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcColorDao implements ColorDao {
    private final ColorsExtractor colorExtractor = new ColorsExtractor();
    private final ConnectionPool connectionPool = ConnectionPool.getInstance();
    private static final String GET_QUERY = "select COLORS.ID, COLORS.CODE " +
            "from (select * from PHONE2COLOR where PHONEID = ?) p2c " +
            "left join COLORS on p2c.COLORID = COLORS.ID order by COLORS.ID";


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
        return colors;
    }

}
