package StockChecker.websites;

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

        return numberLeft.size() != 0;

    }

    @Override
    public boolean isoutofStock() {
        doc = getDoc();

        Element qtyBox = doc.getElementById("qty");

        return qtyBox == null;

    }
}
