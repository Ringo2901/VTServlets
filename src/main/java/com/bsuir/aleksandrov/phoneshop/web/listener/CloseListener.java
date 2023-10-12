package com.bsuir.aleksandrov.phoneshop.web.listener;

import com.bsuir.aleksandrov.phoneshop.model.utils.ConnectionPool;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.SQLException;

public class CloseListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            ConnectionPool.getInstance().closeAllConnections();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
