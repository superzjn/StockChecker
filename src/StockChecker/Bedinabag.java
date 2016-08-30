package StockChecker;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by jzhang9 on 8/26/2016.
 */
public class Bedinabag extends Website {

    public Bedinabag(String url) {
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

        return qtyBox == null;  //return true if got null

    }
}
