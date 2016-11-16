package StockChecker.websites;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;

/**
 * Created by jzhang9 on 8/24/2016.
 */
public class Redsgear extends Website {

    public Redsgear(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {

        try {
            doc = getDoc();
        } catch (SocketTimeoutException e) {
            System.out.println("Time out catch from website sub class" + getUrl());
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

        return qtyBox == null;

    }
}
