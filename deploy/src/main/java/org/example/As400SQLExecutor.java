package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class As400SQLExecutor {
    public void executeQuery(String host, String user, String password, String sql) {
        try {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", password);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:as400://" + host, connectionProps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
