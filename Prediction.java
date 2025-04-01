import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import java.util.ArrayList;
import java.util.List;

/**
 * This class generates a prediction for future years based on the
 * data given in the CSV files.
 *
 * @author Nicolás Alcalá Olea
 */
public class Prediction {

    private static final int MIN_X = 510394;
    private static final int MAX_X = 554000;
    private static final int MIN_Y = 168000;
    private static final int MAX_Y = 194000;

    private DataAggregator dataAggregator;
    private String yearSelected;

    public Prediction(DataAggregator dataAggregator, String yearSelected) {
        this.dataAggregator = dataAggregator;
        this.yearSelected = yearSelected;
        showLoadingPopup();
    }

    protected void showLoadingPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Loading");
        alert.setHeaderText(null);
        alert.setContentText("Generating predictions... Please wait.");
        alert.getDialogPane().setPrefSize(250, 100);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                dataPointList();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(alert::close);
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    alert.setContentText("Error generating predictions.");
                    alert.getDialogPane().setGraphic(null);
                });
            }
        };

        new Thread(task).start();
        alert.show();
    }

    public void dataPointList() {

        String[] pollutantsList = new String[]{"pm2.5", "pm10", "no2"};
        for (String pollutant : pollutantsList) {
            DataSet newDataSet = new DataSet(pollutant, yearSelected, "annual mean", "ug m-3");
            for (int x = MIN_X; x <= MAX_X; x += 1010) {
                for (int y = MIN_Y; y <= MAX_Y; y += 1010) {
                    DataPoint dp = null;
                    List<DataPoint> pastDataPoints = new ArrayList<>();

                    for (int year = 2018; year <= 2023; year++) {
                        DataSet dataSet = dataAggregator.getCityDataSet("London", String.valueOf(year), pollutant);
                        if (dataSet != null) {
                            dp = dataSet.findNearestDataPoint(x, y);
                            if (dp != null) {
                                pastDataPoints.add(dp);

                            }
                        }
                    }
                    if (dp != null) {
                        String gridCode = String.valueOf(dp.gridCode());
                        String X = String.valueOf(dp.x());
                        String Y = String.valueOf(dp.y());
                        String value = String.valueOf(calculatePrediction(pastDataPoints));

                        newDataSet.addData(new String[]{gridCode, X, Y, value});
                    }
                }
            }
            dataAggregator.addDataSet(newDataSet);
        }
    }

    private double calculatePrediction(List<DataPoint> dataPoints) {
        int[] years = {2018, 2019, 2020, 2021, 2022, 2023};
        if (dataPoints.isEmpty())
            return 0;

        int n = Math.min(years.length, dataPoints.size());
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = years[i];
            double y = dataPoints.get(i).value();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        return slope * Integer.parseInt(yearSelected) + intercept; // Predict for the selected year
    }
}