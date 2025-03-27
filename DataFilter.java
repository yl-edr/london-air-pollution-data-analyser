import java.util.HashMap;

public class DataFilter {
    // London Map Boundaries
    private static final int MIN_X = 510394;
    private static final int MAX_X = 554000;
    private static final int MIN_Y = 168000;
    private static final int MAX_Y = 194000;

    private static final HashMap<String, int[]> CITY_BOUNDARIES = new HashMap<>();

    static {
        // Add boundaries for different cities (adjust values as needed)
        CITY_BOUNDARIES.put("London", new int[]{510394, 554000, 168000, 194000});
        CITY_BOUNDARIES.put("Manchester", new int[]{376000, 390901, 393400, 401667});
    }


    public static HashMap<String, DataSet> filterCityData(DataSet dataSet) {
        HashMap<String, DataSet> cityDataSets = new HashMap<>();

        // Create empty datasets for each city
        for (String city : CITY_BOUNDARIES.keySet()) {
            cityDataSets.put(city, new DataSet(dataSet.getPollutant(), dataSet.getYear(), dataSet.getMetric(), dataSet.getUnits()));
        }
        // Filter data points into correct city dataset
        for (DataPoint dp : dataSet.getData()) {
            for (String city : CITY_BOUNDARIES.keySet()) {
                int[] bounds = CITY_BOUNDARIES.get(city);
                if (dp.x() >= bounds[0] && dp.x() <= bounds[1] && dp.y() >= bounds[2] && dp.y() <= bounds[3]) {
                    cityDataSets.get(city).addData(dataPointArray(dp));
                }
            }
        }
        return cityDataSets;
    }

    private static String[] dataPointArray(DataPoint dp){
        return new String[]{dp.gridCode()+"",dp.x()+"",dp.y()+"",dp.value()+""};
    }
}
