package StockChecker;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by jzhang9 on 8/24/2016.
 */
public class Redsgear extends Website {

    public Redsgear(String url) {
        super(url);
    }

    @Override
    public boolean isoutOfstock() {

        doc = getDoc();

        Elements content = doc.getElementsByClass("availability-only");

        if (content.size() != 0) {
            return true;
        }

        return false;
    }
}
