package StockChecker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Controller {
    @FXML
    private TextArea inputUrls;
    @FXML
    private TextArea outputUrls;
    @FXML
    private Label msgBox;
    @FXML
    private TextField searchBox;
    @FXML
    private ProgressBar progressBar;

    private MySQLConnector sqlConnector;
    private String sql = null;

    private ArrayList<String> removedProducts = new ArrayList<>();
    private ArrayList<String> oosProducts = new ArrayList<>();

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

        outputUrls.clear();
        sqlConnector = new MySQLConnector();
        sql = "SELECT * FROM watchlist";
        ResultSet resultSet;
        Website productPage = null;
        int progress = 0;
        int count = 0;

        try {
            resultSet = sqlConnector.query(sql);
//            resultSet.last();
//            count = resultSet.getRow();
//            resultSet.first();

            while (resultSet.next()) {
                String url = resultSet.getString(1);
                if (url.contains("redsgear")) {
                    productPage = new Redsgear(url);
                } else {
                    System.out.println("Website is not supported");
                }
                checkproductPage(productPage);
//                progress++;
//                progressBar.setProgress(progress/count);
            }

            sqlConnector.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        outputUrls.appendText("Removed:" + "\n");
        showcheckResults(removedProducts);
        outputUrls.appendText("Out of Stock" + "\n");
        showcheckResults(oosProducts);
    }


    private void checkproductPage(Website productPage) {
        if (productPage.pagenotFound()) {   // If it returns 404
            removedProducts.add(productPage.getUrl());
        }

        if (productPage.isoutOfstock()) {   // If it is out of stock
            oosProducts.add(productPage.getUrl());
        }
    }

    private void showcheckResults(ArrayList<String> list) {

        for (String url : list) {
            outputUrls.appendText(url + "\n");
        }
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
