public class Manchester extends City {
    // min and max Y are swapped because the top left of the image is actually the max Y
    private static final int MIN_X = 376000;
    private static final int MAX_X = 390901;
    private static final int MIN_Y = 401667;
    private static final int MAX_Y = 393400;
    private static final int RATIO = 3;
    private static final int[] CITY_BOUNDARIES = {MIN_X, MAX_X, MIN_Y, MAX_Y, RATIO};

    public Manchester(DataAggregator dataAggregator) {
        super("Manchester", CITY_BOUNDARIES, dataAggregator);
    }

    
}
