package StockChecker.websites;


/**
 * Created by jzhang9 on 11/9/2016.
 */
public class Sears extends Website {

    public Sears(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {
        return false;
    }



    @Override
    public boolean isoutofStock() {
        try {
            html = parseDoc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int count = countSubstring("Temporarily unavailable", html);
        return count > 0;
    }

    private static int countSubstring(String subStr, String str) {
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }

}
