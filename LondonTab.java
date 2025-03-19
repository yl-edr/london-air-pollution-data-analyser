public class LondonTab extends City {

    // min and max Y are swapped because the top left of the image is actually the max Y
    private static final int MIN_X = 510394;
    private static final int MAX_X = 553297;
    private static final int MIN_Y = 193305;
    private static final int MAX_Y = 168504;
    private static final int RATIO = 1;
    private static final int[] CITY_BOUNDARIES = {MIN_X, MAX_X, MIN_Y, MAX_Y,RATIO};

    public LondonTab(DataAggregator dataAggregator) {
        super("London", CITY_BOUNDARIES, dataAggregator);
    }
    
}
