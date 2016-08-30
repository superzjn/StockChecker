package StockChecker;

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
    private Button deleteButton;
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
    private ArrayList<String> lowProducts = new ArrayList<>();
    private ArrayList<String> oosProducts = new ArrayList<>();

    @FXML
    public void initialize() {
        addButton.setDisable(true);
        deleteButton.setDisable(true);
        searchButton.setDisable(true);
    }


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
        removedProducts.clear();
        oosProducts.clear();
        lowProducts.clear();

        sqlConnector = new MySQLConnector();
        sql = "SELECT * FROM watchlist";
        //ResultSet resultSet;


        Task task = new Task<Void>() {    //Backend Thread
            @Override
            public Void call() {
                Website productPage = null;
                int progress = 0;
                int count;

                try {
                    ResultSet resultSet = sqlConnector.query(sql);
                    ResultSet rowCount = sqlConnector.query("select COUNT(*) from watchlist");
                    rowCount.next();   // Basically you are positioning the cursor before the first row and then requesting data. You need to move the cursor to the first row.
                    count = rowCount.getInt(1);

                    // System.out.println("Count is " + count);

                    while (resultSet.next()) {

                        String url = resultSet.getString(1);
                        if (url.contains("redsgear")) {
                            productPage = new Redsgear(url);
                        } else if (url.contains("bedinabag")) {
                            productPage = new Bedinabag(url);
                        } else {
                            System.out.println("Website is not supported");
                        }
                        checkproductPage(productPage);
                        progress++;
                        updateProgress(progress, count);
                        //   System.out.println("Progress is " + progress);

                    }

                    sqlConnector.close();
                    resultSet.close();

                    Platform.runLater(new Runnable() {         // Go back to UI Thread and update UI
                        @Override
                        public void run() {
                            outputUrls.appendText("Removed:" + "\n");
                            showcheckResults(removedProducts);
                            outputUrls.appendText("Out of Stock:" + "\n");
                            showcheckResults(oosProducts);
                            outputUrls.appendText("Few Left" + "\n");
                            showcheckResults(lowProducts);
                            msgBox.setText("Check Completed!!");
                        }
                    });

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }


//        Runnable task = new Runnable() {    //Backend Thread
//            @Override
//            public void run() {
//                Website productPage = null;
//                int progress = 0;
//
//                try {
//                    ResultSet resultSet = sqlConnector.query(sql);
////            resultSet.last();
////            count = resultSet.getRow();
////            resultSet.first();
//
//                    while (resultSet.next()) {
//
//                        String url = resultSet.getString(1);
//                        if (url.contains("redsgear")) {
//                            productPage = new Redsgear(url);
//                        } else if (url.contains("bedinabag")) {
//                            productPage = new Bedinabag(url);
//                        } else {
//                            System.out.println("Website is not supported");
//                        }
//                        checkproductPage(productPage);
//                        progress++;
//                        int finalProgress = progress;
//
//                                System.out.println("Progress is " + finalProgress);
//                                progressBar.setProgress(finalProgress/5);
//
//
//                    }
//
//                    sqlConnector.close();
//                    resultSet.close();
//
//                    Platform.runLater(new Runnable() {         // Go back to UI Thread and update UI
//                        @Override
//                        public void run() {
//                            outputUrls.appendText("Removed:" + "\n");
//                            showcheckResults(removedProducts);
//                            outputUrls.appendText("Out of Stock:" + "\n");
//                            showcheckResults(oosProducts);
//                            outputUrls.appendText("Few Left" + "\n");
//                            showcheckResults(lowProducts);
//                            msgBox.setText("Check Completed!!");
//                        }
//                    });
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        };

//        new Thread(task).start();
//
//    }


    private void checkproductPage(Website productPage) {
        if (productPage.pagenotFound()) {   // If it returns 404
            removedProducts.add(productPage.getUrl());
            return;
        }

        if (productPage.isalmostGone()) {   // If it is almost gone
            lowProducts.add(productPage.getUrl());
            return;
        }

        if (productPage.isoutofStock()) {
            oosProducts.add(productPage.getUrl());
            return;
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

    public void checkEmptyInput() {
        String input = inputUrls.getText();
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
