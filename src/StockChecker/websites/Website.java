package StockChecker.websites;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by jzhang9 on 8/24/2016.
 */
public abstract class Website {

    private String url;
    private String oosMessage;
    private int statusCode;
    private Connection httpConn;
    Document doc;
    String html;

    Website(String url) {
        this.url = url;
        this.httpConn = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36").timeout(40000);
        //  this.httpConn = Jsoup.connect(url).timeout(40000);

    }

    public abstract boolean isalmostGone();

    public abstract boolean isoutofStock();

    public boolean pagenotFound() {
        statusCode = this.getStatusCode();
        return statusCode == 404;   //return true if got 404
    }

    Document getDoc() throws SocketTimeoutException {   //Get webpage content

        try {
            doc = this.httpConn.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36").get();
            // doc = this.httpConn.get();

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            try {
                System.out.println("Trying again :" + url);
                doc = this.httpConn.get();
            } catch (Exception ex) {
                ex.getStackTrace();
            }
            //  System.out.println("Time out, throwing exception");
            throw new SocketTimeoutException("Webpage timeout");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }


    String parseDoc() throws Exception {   //Get webpage content
        try {
//            System.setProperty("webdriver.ie.driver", "resources/selenium/webdriver/IEDriverServer.exe");
//            WebDriver dr = new InternetExplorerDriver();

//            System.setProperty("webdriver.gecko.driver", "resources/selenium/webdriver/geckodriver.exe");
//           WebDriver dr = new FirefoxDriver();

            System.setProperty("webdriver.chrome.driver", "resources/selenium/webdriver/chromedriver.exe");
            WebDriver dr = new ChromeDriver();

            dr.get(url);
            html = dr.getPageSource();
            dr.close();
            dr.quit();   // end the process
        } catch (Exception e) {
            e.printStackTrace();
        }
        return html;
    }


    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public Connection getHttpConn() {
        return httpConn;
    }

    public void setHttpConn(Connection httpConn) {
        this.httpConn = httpConn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private int getStatusCode() {

        try {
            this.statusCode = httpConn.ignoreHttpErrors(true).execute().statusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


    public String getOosMessage() {
        return oosMessage;
    }

    public void setOosMessage(String oosMessage) {
        this.oosMessage = oosMessage;
    }
}
