package StockChecker.websites;

import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

/**
 * Created by Matus Z on 12/2/2016.
 */
public class BedBathBeyond extends Website {

    public BedBathBeyond(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {
//        try {
//            doc = getDoc();
//        } catch (SocketTimeoutException e) {
//            System.out.println("Time out catch from website sub class");
//        }
//        Elements numberLeft = doc.getElementsMatchingText(Pattern.compile("ONLY \\d LEFT!"));
//        return numberLeft.size() != 0;   //return true if the message exists
return  false;

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
        //For homedepot, if "out of stcok" appears 4 times, then the item is OOS stock.
        // In fact, if "out of stcok" appears 1 time,it is still in stock
        return count > 3;
    }

    private static int countSubstring(String subStr, String str) {
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }
}
