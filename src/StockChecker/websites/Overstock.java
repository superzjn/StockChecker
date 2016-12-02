package StockChecker.websites;

import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

/**
 * Created by Matus Z on 12/2/2016.
 */
public class Overstock extends Website {

    public Overstock(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {
        try {
            doc = getDoc();
        } catch (SocketTimeoutException e) {
            System.out.println("Time out catch from website sub class");
        }
        Elements numberLeft = doc.getElementsMatchingText(Pattern.compile("ONLY \\d LEFT!"));
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

        return oosMessage.size() != 0; //return true if the message exists
    }
}
