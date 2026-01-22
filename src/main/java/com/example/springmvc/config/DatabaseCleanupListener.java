package com.example.springmvc.config;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DatabaseCleanupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("--- ĐANG DỌN DẸP KẾT NỐI DATABASE ---");

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println("Đã hủy driver: " + driver);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
            System.out.println("Đã tắt MySQL Cleanup Thread thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}