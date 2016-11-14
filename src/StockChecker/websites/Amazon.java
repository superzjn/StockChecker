package StockChecker.websites;

import org.jsoup.select.Elements;

import java.util.regex.Pattern;

/**
 * Created by jzhang9 on 8/26/2016.
 */
public class Amazon extends Website {

    public Amazon(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {
        doc = getDoc();

        Elements numberLeft = doc.getElementsMatchingText(Pattern.compile("Only \\d left in stock"));
        return numberLeft.size() != 0;   //return true if the message exists

    }

    @Override
    public boolean isoutofStock() {
        return false;
    }

}


