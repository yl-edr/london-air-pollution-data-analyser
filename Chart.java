import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.HashMap;
import java.util.Map;

public class Chart {
    private LineChart<Number, Number> lineChart;
    private Map<String, XYChart.Series<Number, Number>> seriesMap;

    public Chart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time (in years)");
        yAxis.setLabel("Average Pollution Level (in µg/m³)");

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(2018);
        xAxis.setUpperBound(2023);
        xAxis.setTickUnit(1);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(30);

        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number value) {
                return String.valueOf(value.intValue()); // Convert to int to remove decimal
            }
        });

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setPrefSize(500, 1000);
        lineChart.setCreateSymbols(true);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);

        seriesMap = new HashMap<>();
    }

    public void updateChart(HashMap<String, DataSet> dataRange) {
        lineChart.getData().clear();
        seriesMap.clear();

        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            String[] keyParts = entry.getKey().split("-");
            int year = Integer.parseInt(keyParts[0]);
            String pollutant = keyParts[1];

            seriesMap.putIfAbsent(pollutant, new XYChart.Series<>());
            XYChart.Series<Number, Number> series = seriesMap.get(pollutant);
            series.setName(pollutant.toUpperCase());

            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);

            series.getData().add(new XYChart.Data<>(year, avgValue));
        }

        lineChart.getData().addAll(seriesMap.values());
    }

    public LineChart<Number, Number> getChart() {
        return lineChart;
    }
}
