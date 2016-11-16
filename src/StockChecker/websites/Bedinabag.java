package StockChecker.websites;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;

/**
 * Created by jzhang9 on 8/26/2016.
 */
public class Bedinabag extends Website {

    public Bedinabag(String url) {
        super(url);
    }

    @Override

    public boolean isalmostGone() {

        try {
            doc = getDoc();
        } catch (SocketTimeoutException e) {
            System.out.println("Time out catch from website sub class");
        }

        Elements numberLeft = doc.getElementsByClass("availability-only");

        return numberLeft.size() != 0;

    }

    @Override
    public boolean isoutofStock() {

        try {
            doc = getDoc();
        } catch (SocketTimeoutException e) {
            System.out.println("Time out catch from website sub class");
        }

        Element qtyBox = doc.getElementById("qty");

        return qtyBox == null;  //return true if got null

    }
}
