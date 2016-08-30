package StockChecker;

/**
 * Created by jzhang9 on 8/26/2016.
 */
public class Walmart extends Website {

    public Walmart(String url) {
        super(url);
    }

    @Override
    public boolean isalmostGone() {
        return false;
    }

    @Override
    public boolean isoutofStock() {
        return false;
    }

}
