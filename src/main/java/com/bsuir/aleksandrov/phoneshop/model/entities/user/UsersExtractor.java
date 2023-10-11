package com.bsuir.aleksandrov.phoneshop.model.entities.user;

import com.bsuir.aleksandrov.phoneshop.model.dao.PhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.UserDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcPhoneDao;
import com.bsuir.aleksandrov.phoneshop.model.dao.impl.JdbcUserDao;
import com.bsuir.aleksandrov.phoneshop.model.entities.stock.Stock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersExtractor {

    public List<User> extractData(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            User user = new User();
            user.setUserRole(UserRole.user);
            user.setLogin(resultSet.getString("login"));
            user.setPassword(resultSet.getString("password"));
            users.add(user);
        }
        return users;
    }
}
