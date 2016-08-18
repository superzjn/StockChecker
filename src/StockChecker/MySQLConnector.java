package StockChecker;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jzhang9 on 8/18/2016.
 */
public class MySQLConnector {

    private Connection connection = null;  // Database connection
    private PreparedStatement pst = null;  // SQL statement

    private static final String drivername = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/stockchecker";  // Database location
    private static final String user = "root";      // username
    private static final String password = "root";   // password

    public MySQLConnector() {
        try {
            Class.forName(drivername);// 指定连接类型
            connection = DriverManager.getConnection(url, user, password);

            pst = connection.prepareStatement("select * from watchlist");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally

        {

            try {

                if (pst != null) {
                    pst.close();
                }

                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException ex) {

                Logger lgr = Logger.getLogger(Main.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

}

