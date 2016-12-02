package StockChecker;

import StockChecker.websites.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Controller {
    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button cleandbButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextArea inputUrlsBox;
    @FXML
    private TextArea outputUrlsBox;
    @FXML
    private TextArea removedItemsBox;
    @FXML
    private TextArea oosItemsBox;
    @FXML
    private TextArea lowItemsBox;
    @FXML
    private TextArea notSupportedBox;
    @FXML
    private Label msgBox;
    @FXML
    private Label prgressText;
    @FXML
    private TextField searchBox;
    @FXML
    private ProgressBar progressBar;

    private MySQLConnector sqlConnector;
    private String sql = null;

    private ArrayList<String> removedProducts = new ArrayList<>();
    private ArrayList<String> lowProducts = new ArrayList<>();
    private ArrayList<String> oosProducts = new ArrayList<>();
    private ArrayList<String> notSupportedurls = new ArrayList<>();

    @FXML
    public void initialize() {
        addButton.setDisable(true);
        deleteButton.setDisable(true);
        searchButton.setDisable(true);
    }


    public void showAll() {

        outputUrlsBox.clear();
        sqlConnector = new MySQLConnector();
        sql = "SELECT * FROM watchlist";
        ResultSet resultSet;
        int count = 0;

        try {
            resultSet = sqlConnector.query(sql);

            while (resultSet.next()) {
                outputUrlsBox.appendText(resultSet.getString(1) + "\n");
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

        String input = inputUrlsBox.getText();

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

        String input = inputUrlsBox.getText();
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

        //UI clear
        removedItemsBox.clear();
        oosItemsBox.clear();
        lowItemsBox.clear();
        notSupportedBox.clear();

        removedProducts.clear();
        oosProducts.clear();
        lowProducts.clear();
        notSupportedurls.clear();

        sqlConnector = new MySQLConnector();
        sql = "SELECT * FROM watchlist";
        //ResultSet resultSet;

        Task task = new Task<Void>() {    //Backend Thread
            @Override
            public Void call() {

                float progress = 0;
                int count;

                try {
                    ResultSet resultSet = sqlConnector.query(sql);
                    ResultSet rowCount = sqlConnector.query("select COUNT(*) from watchlist");
                    rowCount.next();   // Basically you are positioning the cursor before the first row and then requesting data. You need to move the cursor to the first row.
                    count = rowCount.getInt(1);

                    // System.out.println("Count is " + count);

                    while (resultSet.next()) {
                        Website productPage = null;
                        String url = resultSet.getString(1);
                        if (url.contains("redsgear")) {
                            productPage = new Redsgear(url);
                        } else if (url.contains("bedinabag")) {
                            productPage = new Bedinabag(url);
                        } else if (url.contains("walmart")) {
                            productPage = new Walmart(url);
                        } else if (url.contains("homedepot")) {
                            productPage = new Homedepot(url);
                        } else if (url.contains("amazon")) {
                            productPage = new Amazon(url);
                        } else if (url.contains("lowes")) {
                            productPage = new Lowes(url);
                        } else if (url.contains("sears")) {
                            productPage = new Sears(url);
                        } else if (url.contains("overstock")) {
                            productPage = new Overstock(url);
                        } else if (url.contains("bedbathandbeyond")) {
                            productPage = new BedBathBeyond(url);
                        } else {
                            notSupportedurls.add(url);
                            //  System.out.println("Website is not supported");
                            notSupportedBox.appendText(url + "\n");
                        }

                        checkproductPage(productPage);

                        progress++;
                        float percent = (100 * progress) / (float) count;

                        Platform.runLater(new Runnable() {         // Go back to UI Thread and update UI
                            @Override
                            public void run() {
                                msgBox.setText(url);    // Current url is being checked
                                prgressText.setText(String.format("%.0f%%", percent));   // Progress percent
                            }

                        });
                        updateProgress(progress, count);
                    }

                    sqlConnector.close();
                    resultSet.close();

                } catch (
                        SQLException e)

                {
                    e.printStackTrace();
                }
                return null;
            }
        };

        progressBar.progressProperty().

                bind(task.progressProperty());
        new

                Thread(task).

                start();

    }


    private void checkproductPage(Website productPage) {
        if (productPage == null) {
            return;
        }

        if (productPage.pagenotFound()) {   // If it returns 404
            removedProducts.add(productPage.getUrl());
            removedItemsBox.appendText(productPage.getUrl() + "\n");
            return;
        }

        if (productPage.isalmostGone()) {   // If it is almost gone
            lowProducts.add(productPage.getUrl());
            lowItemsBox.appendText(productPage.getUrl() + "\n");
            return;
        }

        if (productPage.isoutofStock()) {
            oosProducts.add(productPage.getUrl());
            oosItemsBox.appendText(productPage.getUrl() + "\n");
            return;
        }
    }

    private void showcheckResults(ArrayList<String> list) {

        for (String url : list) {
            outputUrlsBox.appendText(url + "\n");
        }
    }


    public void searchUrl() {

        sqlConnector = new MySQLConnector();
        String input = searchBox.getText();
        ResultSet resultSet;

        if (!input.isEmpty()) {
            input = input.trim();

            outputUrlsBox.clear();
            sql = "SELECT * FROM watchlist WHERE Urls ='" + input + "'";
            resultSet = sqlConnector.query(sql);

            try {
                if (resultSet.next()) {
                    outputUrlsBox.setText("It's there");
                } else {
                    outputUrlsBox.setText("Not Exist");
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


    public void cleanDB() {
        sqlConnector = new MySQLConnector();
        sql = "DELETE FROM  watchlist";

        if (sqlConnector.update(sql) == 1) {
            msgBox.setText("All Records cleared.");
        }
    }

    public void clearUI() {
        removedItemsBox.clear();
        oosItemsBox.clear();
        lowItemsBox.clear();
        notSupportedBox.clear();
        outputUrlsBox.clear();
        searchBox.clear();
        inputUrlsBox.clear();
    }

    public void checkEmptyInput() {
        String input = inputUrlsBox.getText();
        boolean disableButtons = input.isEmpty() || input.trim().isEmpty();
        addButton.setDisable(disableButtons);
        deleteButton.setDisable(disableButtons);
    }

    public void checkEmptySearchInput() {
        String input = searchBox.getText();
        boolean disableButtons = input.isEmpty() || input.trim().isEmpty();
        searchButton.setDisable(disableButtons);
    }
}
