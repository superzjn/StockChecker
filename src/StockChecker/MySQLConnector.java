package StockChecker;

import java.sql.*;

/**
 * Created by jzhang9 on 8/18/2016.
 */
public class MySQLConnector {

    private Connection connection = null;  // Database connection
    private PreparedStatement preparedStatement = null;  // SQL statement

    private static final String driverName = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/stockchecker";  // Database location
    private static final String user = "root";      // username
    private static final String password = "root";   // password

    public MySQLConnector() {
        try {
            Class.forName(driverName);//  Specify connection type
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("Failed to Establish Databse Connection");
            e.printStackTrace();
        }
    }

    public void close() {
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
    public ResultSet query(String sql) {

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
    public boolean update(String sql) {
        boolean flag = false;

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            flag = true;
        } catch (Exception e) {
            System.out.println("Update Execution Failed");
            e.printStackTrace();
        }
        return flag;

    }
}



