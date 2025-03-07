public class DataFilter {
    // London Map Boundaries
    private static final int MIN_X = 510394;
    private static final int MAX_X = 553297;
    private static final int MIN_Y = 168504;
    private static final int MAX_Y = 193305;

    public static DataSet filterLondonData(DataSet dataSet) {
        DataSet londonDataSet = new DataSet(dataSet.getPollutant(), dataSet.getYear(), dataSet.getMetric(), dataSet.getUnits());
        for (DataPoint dp : dataSet.getData()) {
            // Check if the point is within London's boundaries
            if (dp.x() >= MIN_X && dp.x() <= MAX_X && dp.y() >= MIN_Y && dp.y() <= MAX_Y) {
                londonDataSet.addData(dataPointArray(dp));
            }
        }
        return londonDataSet;
    }
    private static String[] dataPointArray(DataPoint dp){
        return new String[]{dp.gridCode()+"",dp.x()+"",dp.y()+"",dp.value()+""};
    }
}
