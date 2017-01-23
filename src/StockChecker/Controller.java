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
    private static final String sqlreturnAll = "SELECT * FROM watchlist";

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
        ResultSet resultSet;
        int count = 0;

        try {
            resultSet = sqlConnector.query(sqlreturnAll);

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
            String[] urls = input.trim().split("[\\r\\n]+");     // make it works for Windows, UNIX and Mac, and ignore empty lines
            count = urls.length;

            for (String str : urls) {

                str = str.trim();    //Remove spaces
                String sql = "INSERT INTO watchlist VALUES('" + str + "',now());";

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
                String sql = "DELETE FROM watchlist WHERE Urls = '" + str + "';";

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

    //Check if the item back in stock
    public void checkifbackinStock() {

        ArrayList<String> backinstockItems = new ArrayList<>();

        sqlConnector = new MySQLConnector();
        ResultSet resultSet = sqlConnector.query(sqlreturnAll);
        int count = geturlCount();

        Task task = new Task<Void>() {    //Backend Thread
            @Override
            public Void call() {

                float progress = 0;

                try {
                    while (resultSet.next()) {
                        String url = resultSet.getString(1);
                        Website productPage = detectWebsite(url);
                        if (productPage.pagenotFound()) {   // If it returns 404
                            removedProducts.add(productPage.getUrl());
                            removedItemsBox.appendText(productPage.getUrl() + "\n");
                        } else if (!productPage.isoutofStock()) {
                            backinstockItems.add(productPage.getUrl());
                            notSupportedBox.appendText(productPage.getUrl() + "\n");  // Add backin Stock item to "Not supported websites" box
                        }

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


                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        System.out.println("In finally block");
                        e.printStackTrace();
                    }
                    sqlConnector.close();
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

    public void checkStock() {

        clearResultBoxes();

        removedProducts.clear();
        oosProducts.clear();
        lowProducts.clear();
        notSupportedurls.clear();

        sqlConnector = new MySQLConnector();
        ResultSet resultSet = sqlConnector.query(sqlreturnAll);
        int count = geturlCount();

        Task task = new Task<Void>() {    //Backend Thread
            @Override
            public Void call() {

                float progress = 0;
                try {
                    while (resultSet.next()) {
                        String url = resultSet.getString(1);
                        checkproductPage(url);
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

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        resultSet.close();
                        sqlConnector.close();
                    } catch (SQLException e) {
                        System.out.println("In finally block");
                        e.printStackTrace();
                    }

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

    private int geturlCount() {
        sqlConnector = new MySQLConnector();
        int count = 0;

        ResultSet resultSet = sqlConnector.query("select COUNT(*) from watchlist");

        try {
            resultSet.next();
            count = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("Exception in final block");
                e.printStackTrace();
            }
            sqlConnector.close();
        }
        return count;
    }


    private Website detectWebsite(String url) {
        Website productPage;
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
            productPage = null;
        }
        return productPage;
    }

    private void checkproductPage(String url) {

        Website productPage = detectWebsite(url);

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
            String sql = "SELECT * FROM watchlist WHERE Urls ='" + input + "'";
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
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    System.out.println("In finally block");
                    e.printStackTrace();
                }
                sqlConnector.close();
            }

        } else {
            msgBox.setText("Please Input");
        }
    }


    public void cleanDB() {
        sqlConnector = new MySQLConnector();
        String sql = "DELETE FROM  watchlist";

        if (sqlConnector.update(sql) == 1) {
            msgBox.setText("All Records cleared.");
        }
    }

    //Clear entire UI
    public void clearUI() {
        clearResultBoxes();
        outputUrlsBox.clear();
        searchBox.clear();
        inputUrlsBox.clear();
    }

    //Clear result boxes
    private void clearResultBoxes() {
        removedItemsBox.clear();
        oosItemsBox.clear();
        lowItemsBox.clear();
        notSupportedBox.clear();

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
