import java.util.ArrayList;
import java.util.List;

/**
 * This class generates a prediction for future years based on the
 * data given in the CSV files... MOOORE
 *
 * @author Nicolás Alcalá Olea
 */

public class Prediction {

    private static final int MIN_X = 510394;
    private static final int MAX_X = 554000;
    private static final int MIN_Y = 168000;
    private static final int MAX_Y = 194000;

    private DataAggregator dataAggregator;

    public Prediction() {

    }

    public void dataPointList() {
        String[] pollutantsList = new String[]{"pm2.5", "pm10", "no2"};
        for (String pollutants : pollutantsList){
            List<DataPoint> newDataPoints = new ArrayList<>();
            for (int x = MIN_X; x <= MAX_X; x+=17){
                for (int y = MIN_Y; y <= MAX_Y; y+=20){
                    for (int year = 2018; year <= 2023; year++){
                        DataSet dataSet = dataAggregator.getCityDataSet("London", Integer.toString(year), pollutants);
                        // List<DataPoint> dataPoints = dataSet.getData();
                        newDataPoints.add(dataSet.findNearestDataPoint(x, y));
                    }
                    //calculate the next years value

                }
            }
        }
    }

    private double calculatePrediction(List<DataPoint> dataPoints) {
        double prediction = 0;
        for (DataPoint dp : dataPoints) {

        }
        return 0;
    }
}