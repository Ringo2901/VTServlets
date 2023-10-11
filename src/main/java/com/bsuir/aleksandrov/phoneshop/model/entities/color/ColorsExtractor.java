package com.bsuir.aleksandrov.phoneshop.model.entities.color;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColorsExtractor {
    public List<Color> extractData(ResultSet resultSet) throws SQLException {
        List<Color> colors = new ArrayList<>();

        while (resultSet.next()) {
            Color color = new Color();
            color.setId(resultSet.getLong("ID"));
            color.setCode(resultSet.getString("CODE"));
            colors.add(color);
        }
        return colors;
    }
}
