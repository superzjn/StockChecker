package StockChecker.websites;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Pattern;

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


    //Not working, cannnot read HTML content on sears.com
    @Override
    public boolean isoutofStock() {

        doc = getDoc();
        System.out.println(doc.toString());
        Elements oosMessage = doc.getElementsMatchingText(Pattern.compile("Temporarily unavailable"));
        return oosMessage.size() != 0;   //return true if the message exists

    }

}
