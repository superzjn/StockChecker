package StockChecker.websites;

import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

/**
 * Created by Matus on 8/26/2016.
 */
public class Walmart extends Website {

    public Walmart(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {
        try {
            doc = getDoc();
        } catch (SocketTimeoutException e) {
            System.out.println("Time out catch from website sub class");
        }
        Elements numberLeft = doc.getElementsMatchingText(Pattern.compile("Only \\d left!"));
        return numberLeft.size() != 0;   //return true if the message exists

    }

    @Override
    public boolean isoutofStock() {
        try {
            doc = getDoc();
        } catch (SocketTimeoutException e) {
            System.out.println("Time out catch from website sub class");
        }

        Elements oosMessage = doc.getElementsMatchingText(Pattern.compile("Out of stock"));
        return oosMessage.size() != 0;   //return true if the message exists
    }

}
