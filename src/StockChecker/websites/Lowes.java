package StockChecker.websites;

/**
 * Created by Matus Z on 11/22/2016.
 */
public class Lowes extends Website {

    public Lowes(String url) {
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
        int count = countSubstring("Available!", html);

        int count2 = countSubstring("Ready for delivery", html);

        return count+count2 == 0;   // Return true if it cannot find "Available!" or "Ready for delivery"



    }


//    @Override
//    public boolean isoutofStock() {
//        try {
//            html = parseDoc();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        int count = countSubstring("Currently unavailable", html);
//        return count > 0;
//    }

    private static int countSubstring(String subStr, String str) {
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }

}
