import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.util.HashMap;
import java.util.List;

public class Chart {
    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> series;

    public Chart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Year");
        yAxis.setLabel("Pollution Level");

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(2018);
        xAxis.setUpperBound(2023);
        xAxis.setTickUnit(1);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(30);

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);

        series = new XYChart.Series<>();
        lineChart.getData().add(series);
    }

    public void updateChart(HashMap<String, DataSet> dataRange) {
        series.getData().clear();

        List<Integer> years = dataRange.keySet().stream()
                .map(Integer::parseInt)
                .sorted()
                .toList();

        for (int year : years) {
            DataSet dataSet = dataRange.get(String.valueOf(year));
            
            double avgValue = dataSet.getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);

            series.getData().add(new XYChart.Data<>(year, avgValue));
        }
    }

    public LineChart<Number, Number> getChart() {
        return lineChart;
    }
}
