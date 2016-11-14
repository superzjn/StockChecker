package StockChecker.websites;

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
        doc = getDoc();
        String text = doc.toString().toLowerCase();
        int count = countSubstring("out of stock", text);
        //For homedepot, if "out of stcok" appears 6 times, then the item is OOS stock.
        // In fact, if "out of stcok" appears 1 time,it is still in stock
        return count > 3;
    }

    private static int countSubstring(String subStr, String str) {
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }

}
