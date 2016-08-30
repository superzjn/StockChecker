package StockChecker;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by jzhang9 on 8/24/2016.
 */
public abstract class Website {

    private String url;
    private String oosMessage;
    private int statusCode;
    private Connection httpConn;
    protected Document doc;


    public Website(String url) {
        this.url = url;
        this.httpConn = Jsoup.connect(url).timeout(20000);

    }

    public abstract boolean isalmostGone();

    public abstract boolean isoutofStock();

    public boolean pagenotFound() {
        statusCode = this.getStatusCode();
        return statusCode == 404;   //return true if got 404
    }


    public Document getDoc() {   //Get webpage content

        try {
            doc = this.httpConn.get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
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
