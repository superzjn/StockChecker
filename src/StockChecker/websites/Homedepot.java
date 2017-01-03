package StockChecker.websites;

import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

/**
 * Created by Matus Z on 11/7/2016.
 */
public class Homedepot extends Website {

    public Homedepot(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {
        return false;
    }

    @Override

    public boolean isoutofStock() {
        try {
            doc = getDoc();
        } catch (SocketTimeoutException e) {
            System.out.println("Time out catch from website sub class");
        }
        String text = doc.toString().toLowerCase();

        int count = countSubstring("out of stock", text);
        //       int count2 = countSubstring("this item does not qualify for free shipping", text);
// Free shipping is not available and shipping fee is expensive for the following items, they should be removed
        // Elements oosMessage = doc.getElementsMatchingText(Pattern.compile("This item does not qualify for free shipping"));   //Standard shipping
        Elements oosMessage2 = doc.getElementsMatchingText(Pattern.compile("Express Delivery"));

        //For homedepot, if "out of stcok" appears 6 times, then the item is OOS stock.
        // In fact, if "out of stcok" appears 1 time,it is still in stock
        return count > 3;
        //  return count > 3 || count2 != 0;
    }

    private static int countSubstring(String subStr, String str) {
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }

}
