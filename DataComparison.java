import javafx.scene.chart.*;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import java.util.HashMap;

public class DataComparison {

    private HashMap<String, CityData> cityData = new HashMap<>();
    private ArrayList<ArrayList<Double>> pollutantValues = new ArrayList<>();
    private double[] healthyValue = {2, 4400, 40, 60, 20, 10, 20, 40};

    public DataComparison() {
        pollutantValues.add(new ArrayList<>());
        pollutantValues.add(new ArrayList<>());
        pollutantValues.add(new ArrayList<>());
        pollutantValues.add(new ArrayList<>());
        pollutantValues.add(new ArrayList<>());
        pollutantValues.add(new ArrayList<>());
        pollutantValues.add(new ArrayList<>());
        pollutantValues.add(new ArrayList<>());
    }

    public boolean addCityData(String name, double[] data) {
        if (cityData.containsKey(name)) {
            return false;
        }
        else {
            for (int i = 0; i < data.length; i++) {
                pollutantValues.get(i).add(data[i]);
            }
            CityData city = new CityData(name, data);
            cityData.put(name, city);
            return true;
        }
    }

    public boolean removeCityData(String name) {
        if (!cityData.containsKey(name)) {
            return false;
        } else {
            CityData city = cityData.get(name);
            double[] data = city.getData();

            for (int i = 0; i < data.length; i++) {
                pollutantValues.get(i).remove(data[i]);
            }

            cityData.remove(name);
            return true;
        }
    }

    private double minMaxScale(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    public BorderPane createChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Pollutant");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Pollutant Level (Scaled)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Pollutant Levels by City - Above 1 indicates an unhealthy pollutant level");
        barChart.setCategoryGap(30);
        barChart.setBarGap(5);

        String[] pollutantNames = {"AQI", "CO", "NO2", "O3", "SO2", "PM2.5", "PM10", "NH3"};

        xAxis.getCategories().addAll(pollutantNames);

        for (String cityName : cityData.keySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(cityName);

            double[] data = cityData.get(cityName).getData();
            double[] scaledData = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                double scaledValue = minMaxScale(data[i], 0, healthyValue[i]);
                if (scaledValue == 0){
                    scaledValue = 0.01; // Prevents the bar from being invisible when the value is 0
                }
                scaledData[i] = scaledValue;
            }
            for (int i = 0; i < data.length; i++) {
                series.getData().add(new XYChart.Data<>(pollutantNames[i], scaledData[i]));
            }

            barChart.getData().add(series);
        }

        BorderPane pane = new BorderPane();
        pane.setCenter(barChart);
        return pane;
    }
}
