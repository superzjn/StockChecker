package StockChecker.websites;

import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

/**
 * Created by Matus on 8/26/2016.
 */
public class Amazon extends Website {

    public Amazon(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {
        try {
            doc = getDoc();
        } catch (SocketTimeoutException e) {
            System.out.println("Time out catch from website sub class");
        }

        Elements numberLeft = doc.getElementsMatchingText(Pattern.compile("Only \\d left in stock"));
        return numberLeft.size() != 0;   //return true if the message exists

    }

    @Override
    public boolean isoutofStock() {


        try {
            doc = getDoc();
        } catch (SocketTimeoutException e) {
            System.out.println("Time out catch from website sub class");
        }
        System.out.println(doc.toString());
        // 3 kinds of OOS message on amazon
        Elements oosMessage = doc.getElementsMatchingText(Pattern.compile("Currently unavailable"));
        Elements oosMessage2 = doc.getElementsMatchingText(Pattern.compile("Temporarily out of stock."));
        Elements oosMessage3 = doc.getElementsMatchingText(Pattern.compile("Usually ships within"));

        return oosMessage.size() != 0 || oosMessage2.size() != 0 ||oosMessage3.size() != 0;   //return true if the message exists
    }
}


