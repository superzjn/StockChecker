package StockChecker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Controller {
    @FXML
    private TextArea inputUrls;
    @FXML
    private TextArea outputUrls;
    @FXML
    private Label msgBox;
    @FXML
    private TextField searchBox;

    private MySQLConnector sqlConnector;
    private String sql = null;

    public void showAll() {

        outputUrls.clear();
        sqlConnector = new MySQLConnector();
        sql = "SELECT * FROM watchlist";
        ResultSet resultSet = null;
        int count = 0;

        try {
            resultSet = sqlConnector.query(sql);

            while (resultSet.next()) {
                outputUrls.appendText(resultSet.getString(1) + "\n");
            }

            // Get count
            resultSet.last();
            count = resultSet.getRow();

            sqlConnector.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        msgBox.setText("Total of " + count + " urls are shown.");
    }


    public void addUrl() {

        sqlConnector = new MySQLConnector();
        int count = 1;

        String input = inputUrls.getText();
        String[] urls = input.split("[\\r\\n]+");     // make it works for Windows, UNIX and Mac, and ignore empty lines

        for (String str : urls) {
            try {
                str = str.trim();    //Remove spaces
                sql = "INSERT INTO watchlist VALUES('" + str + "',now());";
                if (sqlConnector.update(sql)) {
                    msgBox.setText(count + " urls are added.");
                    count++;
                }
            } catch (Exception e) {
                msgBox.setText("Update Failed.");
                e.printStackTrace();
            } finally {
                sqlConnector.close();
            }
        }
    }

    public void delUrl() {

    }

    public void checkStock() {

    }

    public void searchUrl() {

    }

}
