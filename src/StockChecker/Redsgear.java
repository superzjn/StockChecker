package StockChecker;

/**
 * Created by jzhang9 on 8/24/2016.
 */
public class Redsgear extends Website {

    public Redsgear(String url) {
        super(url);
    }

    @Override
    public boolean isoutOfstock() {
        return false;
    }
}
