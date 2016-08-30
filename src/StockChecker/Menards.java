package StockChecker;

/**
 * Created by jzhang9 on 8/26/2016.
 */
public class Menards extends Website {

    public Menards(String url) {
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


