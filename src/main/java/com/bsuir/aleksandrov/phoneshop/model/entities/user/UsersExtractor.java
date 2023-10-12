package com.bsuir.aleksandrov.phoneshop.model.entities.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersExtractor {

    public List<User> extractData(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setLogin(resultSet.getString("login"));
            user.setPassword(resultSet.getString("password"));
            user.setUserRole(UserRole.valueOf(resultSet.getString("role")));
            users.add(user);
        }
        return users;
    }
}
