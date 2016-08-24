package StockChecker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
        ResultSet resultSet;
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
        int count;

        String input = inputUrls.getText();

        if (!input.isEmpty()) {
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
        } else {
            msgBox.setText("Please Input");
        }
    }

    public void delUrl() {

        sqlConnector = new MySQLConnector();
        int count;

        String input = inputUrls.getText();
        if (!input.isEmpty()) {
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
            msgBox.setText("Please Input");
        }
    }

    public void checkStock() {
        // public void checkStock() throws IOException {
        Connection httpConn = Jsoup.connect("http://www.redsgear.com/z-hunter-assisted-opening-knife-4-5in-closed-zb-003gn.html2");
        Redsgear red = new Redsgear("http://www.redsgear.com/z-hunter-assisted-opening-knife-4-5in-closed-zb-003gnxx.html");
        int code2 = red.getStatusCode();

        if (!red.pagenotFound()) {
            System.out.println("Good");
        }

        int test = 999;
        try {
            test = httpConn.execute().statusCode();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Test " + test);


        //Document doc = httpConn.get();

        //Elements content = doc.getElementsByClass("availability-only");

        //System.out.println(doc.title());
        //System.out.println(content.text());
    }

    public void searchUrl() {

        sqlConnector = new MySQLConnector();
        String input = searchBox.getText();
        ResultSet resultSet;

        if (!input.isEmpty()) {
            input = input.trim();

            outputUrls.clear();
            sql = "SELECT * FROM watchlist WHERE Urls ='" + input + "'";
            resultSet = sqlConnector.query(sql);

            try {
                if (resultSet.next()) {
                    outputUrls.setText("It's there");
                } else {
                    outputUrls.setText("Not Exist");
                }
                resultSet.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                sqlConnector.close();
            }

        } else {
            msgBox.setText("Please Input");
        }


    }

}
