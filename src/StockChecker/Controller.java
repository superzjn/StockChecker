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
        int count = 0;

        String input = inputUrls.getText();
        String[] urls = input.split("[\\r\\n]+");     // make it works for Windows, UNIX and Mac, and ignore empty lines
        count = urls.length;

        for (String str : urls) {

            str = str.trim();    //Remove spaces
            sql = "INSERT INTO watchlist VALUES('" + str + "',now());";

            if (sqlConnector.update(sql) == 1) {
                msgBox.setText(count + " urls are added.");
            } else if (sqlConnector.update(sql) == 2) {
                msgBox.setText(count + " This url is already exist. " + str);
            } else {
                msgBox.setText("Update failed");
            }
        }
        sqlConnector.close();
    }

    public void delUrl() {

        sqlConnector = new MySQLConnector();
        int count = 0;

        String input = inputUrls.getText();
        if (input != "") {
            String[] urls = input.split("[\\r\\n]+");     // make it works for Windows, UNIX and Mac, and ignore empty lines
            count = urls.length;

            for (String str : urls) {

                str = str.trim();    //Remove spaces
                sql = "DELETE FROM watchlist WHERE Urls = '" + str + "';";

                if (sqlConnector.update(sql) == 1) {
                    msgBox.setText(count + " urls are deleted.");
                } else {
                    msgBox.setText("Update failed");
                }
            }
            sqlConnector.close();
        } else {
            msgBox.setText("Please input");
        }

    }

    public void checkStock() {

    }

    public void searchUrl() {

    }

}
