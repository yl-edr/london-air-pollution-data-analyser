import javafx.scene.chart.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class Chart {
    private VBox chartContainer;
    private LineChart<Number, Number> lineChart;
    private BarChart<String, Number> barChart;
    private AreaChart<Number, Number> areaChart;
    private PieChart pieChart;
    
    private Map<String, XYChart.Series<Number, Number>> seriesMap;
    private ChartType currentChartType;

    public enum ChartType {
        LINE, BAR, AREA, PIE
    }

    public Chart() {
        chartContainer = new VBox();
        // Make the chart container expandable
        chartContainer.setFillWidth(true);
        VBox.setVgrow(chartContainer, Priority.ALWAYS);
        
        seriesMap = new HashMap<>();
        
        // Initialize all chart types
        lineChart();
        barChart();
        areaChart();
        pieChart();
        
        // Set default chart type
        setChartType(ChartType.LINE);
    }
    
    private void lineChart() {
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

        // Format the x axis
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number value) {
                return String.valueOf(value.intValue());
            }
        });

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setPrefSize(750, 575);

        // Allow the chart to resize with the window
        lineChart.prefWidthProperty().bind(chartContainer.widthProperty());
        lineChart.prefHeightProperty().bind(chartContainer.heightProperty());
        lineChart.setCreateSymbols(true);
        lineChart.setLegendVisible(true);
        lineChart.setAnimated(false);
        lineChart.setTitle("Pollution Levels Over Time");
    }
    
    private void barChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        
        xAxis.setLabel("Year");
        yAxis.setLabel("Average Pollution Level (in µg/m³)");
        
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(30);
        
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setPrefSize(750, 575);

        // Allow the chart to resize with the window
        barChart.prefWidthProperty().bind(chartContainer.widthProperty());
        barChart.prefHeightProperty().bind(chartContainer.heightProperty());
        barChart.setLegendVisible(true);
        barChart.setAnimated(false);
        barChart.setTitle("Pollution Levels by Year");
    }
    
    private void areaChart() {
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
        
        areaChart = new AreaChart<>(xAxis, yAxis);
        areaChart.setPrefSize(750, 575);

        // Allow the chart to resize with the window
        areaChart.prefWidthProperty().bind(chartContainer.widthProperty());
        areaChart.prefHeightProperty().bind(chartContainer.heightProperty());
        areaChart.setCreateSymbols(true);
        areaChart.setLegendVisible(true);
        areaChart.setAnimated(false);
        areaChart.setTitle("Pollution Levels Trend");
    }
    
    private void pieChart() {
        pieChart = new PieChart();
        pieChart.setPrefSize(750, 575);

        // Allow the chart to resize with the window
        pieChart.prefWidthProperty().bind(chartContainer.widthProperty());
        pieChart.prefHeightProperty().bind(chartContainer.heightProperty());
        pieChart.setLegendVisible(true);
        pieChart.setAnimated(false);
        pieChart.setTitle("Pollution Distribution");
    }
    
    public void setChartType(ChartType type) {
        this.currentChartType = type;
        
        // Clear the container
        chartContainer.getChildren().clear();
        
        // Add the appropriate chart based on type
        switch (type) {
            case LINE:
                chartContainer.getChildren().add(lineChart);
                break;
            case BAR:
                chartContainer.getChildren().add(barChart);
                break;
            case AREA:
                chartContainer.getChildren().add(areaChart);
                break;
            case PIE:
                chartContainer.getChildren().add(pieChart);
                break;
        }
    }

    public void updateChart(HashMap<String, DataSet> dataRange) {
        // Clear existing data from all charts
        lineChart.getData().clear();
        barChart.getData().clear();
        areaChart.getData().clear();
        pieChart.getData().clear();
        seriesMap.clear();
        
        // Update the appropriate chart based on current type
        switch (currentChartType) {
            case LINE:
                updateLineAndAreaChart(dataRange);
                break;
            case BAR:
                updateBarChart(dataRange);
                break;
            case AREA:
                updateLineAndAreaChart(dataRange);
                break;
            case PIE:
                updatePieChart(dataRange);
                break;
        }
    }
    
    private void updateLineAndAreaChart(HashMap<String, DataSet> dataRange) {
        // Clear existing data from the chart
        lineChart.getData().clear();
        areaChart.getData().clear();
        seriesMap.clear();

        // This works for both Line and Area charts since they use the same data structure
        XYChart<Number, Number> chart;
        if (currentChartType == ChartType.LINE) {
            chart = lineChart;
        } else {
            chart = areaChart;
        }
        
        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            String[] keyParts = entry.getKey().split("-");
            int year = Integer.parseInt(keyParts[0]);
            String pollutant = keyParts[1];

            // Create a new series for each pollutant if it doesn't exist
            seriesMap.putIfAbsent(pollutant, new XYChart.Series<>());
            XYChart.Series<Number, Number> series = seriesMap.get(pollutant);
            series.setName(pollutant.toUpperCase());
    
            // Calculate the average value for the pollutant
            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);
    
            // Add the data point to the series
            series.getData().add(new XYChart.Data<>(year, avgValue));
        }
    
        // Add all series to the chart
        chart.getData().addAll(seriesMap.values());
    }
    
    private void updateBarChart(HashMap<String, DataSet> dataRange) {
        Map<String, XYChart.Series<String, Number>> barSeriesMap = new HashMap<>();
        
        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            String[] keyParts = entry.getKey().split("-");
            String year = keyParts[0];
            String pollutant = keyParts[1];
            
            barSeriesMap.putIfAbsent(pollutant, new XYChart.Series<>());
            XYChart.Series<String, Number> series = barSeriesMap.get(pollutant);
            series.setName(pollutant.toUpperCase());
            
            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);
            
            series.getData().add(new XYChart.Data<>(year, avgValue));
        }
        
        barChart.getData().addAll(barSeriesMap.values());
    }
    
    private void updatePieChart(HashMap<String, DataSet> dataRange) {
        pieChart.getData().clear();
        
        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            String[] keyParts = entry.getKey().split("-");
            String pollutant = keyParts[1];
            
            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);
            
            PieChart.Data data = new PieChart.Data(pollutant.toUpperCase(), avgValue);
            pieChart.getData().add(data);
        }
    }

    public ChartType getCurrentChartType() {
        return currentChartType;
    }

    public Pane getChartBox() {
        return chartContainer;
    }
}