import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Chart {
    private LineChart<Number, Number> lineChart;

    public Chart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Year");
        yAxis.setLabel("Pollution Level");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(18);
        xAxis.setUpperBound(23);
        xAxis.setTickUnit(1);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(10);
        yAxis.setUpperBound(40);

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(18, 23));
        series.getData().add(new XYChart.Data<>(19, 14));
        series.getData().add(new XYChart.Data<>(20, 15));
        series.getData().add(new XYChart.Data<>(21, 24));
        series.getData().add(new XYChart.Data<>(22, 34));
        series.getData().add(new XYChart.Data<>(23, 36));

        lineChart.getData().add(series);
    }

    public LineChart<Number, Number> getChart() {
        return lineChart;
    }
}
