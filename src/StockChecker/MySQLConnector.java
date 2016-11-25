package StockChecker;

import java.sql.*;

/**
 * Created by jzhang9 on 8/18/2016.
 */
class MySQLConnector {

    private Connection connection = null;  // Database connection
    private PreparedStatement preparedStatement = null;  // SQL statement

    private static final String driverName = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/";  // Database location
    private static final String user = "root";      // username
    private static final String password = "root";   // password

    MySQLConnector() {
        try {
            Class.forName(driverName);//  Specify connection type
            connection = DriverManager.getConnection(url, user, password);

            preparedStatement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS stockchecker;");
            preparedStatement.execute();

            String newurl = "jdbc:mysql://localhost:3306/stockchecker?autoReconnect=true&useSSL=false";
            connection = DriverManager.getConnection(newurl, user, password);

            preparedStatement = connection.prepareStatement("USE stockchecker;");
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS watchlist (Urls VARCHAR(850), AddDate TIMESTAMP, PRIMARY KEY (Urls));");
            preparedStatement.execute();

        } catch (Exception e) {
            System.out.println("Failed to Establish Databse Connection");
            e.printStackTrace();
        }
    }

    void close() {
        try {

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            System.out.println("Failed to Close Database Connection");
            e.printStackTrace();
        }
    }

    // Show All
    ResultSet query(String sql) {

        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

        } catch (Exception e) {
            System.out.println("Query Execution Failed");
            e.printStackTrace();
        }
        return resultSet;
    }

    //Add or Delete
    int update(String sql) {
        int flag = 0;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            flag = 1;
        } catch (Exception e) {
            System.out.println("Update Execution Failed");
            e.printStackTrace();
            if (e.toString().contains("Duplicate")) {
                System.out.println("Duplicate Records");
                flag = 2;
            }
        }
        return flag;
    }
}



