package StockChecker;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by jzhang9 on 8/24/2016.
 */
public class Redsgear extends Website {

    public Redsgear(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {

        doc = getDoc();

        Elements numberLeft = doc.getElementsByClass("availability-only");

        if (numberLeft.size() != 0) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isoutofStock() {
        doc = getDoc();

        Element qtyBox = doc.getElementById("qty");

        if (qtyBox == null) {
            return true;
        }

        return false;
    }
}
