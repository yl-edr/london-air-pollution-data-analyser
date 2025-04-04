import javafx.scene.chart.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;

import javafx.application.Platform;
import javafx.geometry.Side;


public class Chart {
    private VBox chartContainer;
    private LineChart<Number, Number> lineChart;
    private BarChart<String, Number> barChart;
    private PieChart pieChart;
    
    private Map<String, XYChart.Series<Number, Number>> seriesMap;
    private ChartType currentChartType;

    private HashMap<String, String> pollutantColors = new HashMap<>();

    public enum ChartType {
        LINE, BAR, PIE
    }

    public Chart() {
        chartContainer = new VBox();
        // Make the chart container expandable
        chartContainer.setFillWidth(true);
        VBox.setVgrow(chartContainer, Priority.ALWAYS);
        
        seriesMap = new HashMap<>();

        pollutantColors.put("PM10", "#3498db"); // blue
        pollutantColors.put("PM2.5", "#e74c3c"); // red
        pollutantColors.put("NO2", "#2ecc71"); // green
        
        // Initialize all chart types
        lineChart();
        barChart();
        pieChart();
        
        // Set default chart type
        setChartType(ChartType.LINE);
    }
    
    private void lineChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.getStyleClass().add("xAxisLC");
        yAxis.getStyleClass().add("yAxisLC");

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
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setTitle("Pollution Levels Over Time");
        lineChart.getStyleClass().add("lineChart");
    }
    
    private void barChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.getStyleClass().add("xAxisBC");
        yAxis.getStyleClass().add("yAxisBC");
        
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
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setTitle("Pollution Levels by Year");
        barChart.getStyleClass().add("barChart");
    }
    
    private void pieChart() {
        pieChart = new PieChart();
        pieChart.setPrefSize(750, 575);

        // Allow the chart to resize with the window
        pieChart.prefWidthProperty().bind(chartContainer.widthProperty());
        pieChart.prefHeightProperty().bind(chartContainer.heightProperty());
        pieChart.setLegendVisible(false);
        pieChart.setAnimated(true);
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
            case PIE:
                chartContainer.getChildren().add(pieChart);
                break;
        }
    }

    public void updateChart(HashMap<String, DataSet> dataRange) {
        // Clear existing data from all charts
        lineChart.getData().clear();
        barChart.getData().clear();
        pieChart.getData().clear();
        seriesMap.clear();
        
        // Update the appropriate chart based on current type
        switch (currentChartType) {
            case LINE:
                updateLineChart(dataRange);
                break;
            case BAR:
                updateBarChart(dataRange);
                break;
            case PIE:
                updatePieChart(dataRange);
                break;
        }
    }
    
    private void updateLineChart(HashMap<String, DataSet> dataRange) {

        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            String[] keyParts = entry.getKey().split("-");
            int year = Integer.parseInt(keyParts[0]);
            String pollutant = keyParts[1].toUpperCase();

            // Create a new series for each pollutant if it doesn't exist
            seriesMap.putIfAbsent(pollutant, new XYChart.Series<>());
            XYChart.Series<Number, Number> series = seriesMap.get(pollutant);
    
            // Calculate the average value for the pollutant
            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);
    

            series.setName(pollutant);
            // Add the data point to the series
            series.getData().add(new XYChart.Data<>(year, avgValue));
        }
    
        // Add all series to the chart
        lineChart.getData().addAll(seriesMap.values());
        
        // Apply colors to each series after they are rendered
        Platform.runLater(() -> {
            for (XYChart.Series<Number, Number> series : lineChart.getData()) {
                String pollutant = series.getName();
                String color = pollutantColors.getOrDefault(pollutant, "black");

                Node node = series.getNode();
                if (node != null) {
                    String style = String.format("-fx-stroke: %s;", color);
                    node.setStyle(style);
                }
            }
        });
    }
    
    private void updateBarChart(HashMap<String, DataSet> dataRange) {
        Map<String, XYChart.Series<String, Number>> barSeriesMap = new HashMap<>();
        
        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            String[] keyParts = entry.getKey().split("-");
            String year = keyParts[0];
            String pollutant = keyParts[1].toUpperCase();
            
            barSeriesMap.putIfAbsent(pollutant, new XYChart.Series<>());
            XYChart.Series<String, Number> series = barSeriesMap.get(pollutant);
            
            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);
            

            series.setName(pollutant);        
            series.getData().add(new XYChart.Data<>(year, avgValue));
        }
        
        barChart.getData().addAll(barSeriesMap.values());

        // Apply colors to each series after they are rendered
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                String pollutant = series.getName();
                String color = pollutantColors.getOrDefault(pollutant, "black");
        
                for (XYChart.Data<String, Number> data : series.getData()) {
                    Node barNode = data.getNode();
                    if (barNode != null) {
                        barNode.setStyle(String.format("-fx-bar-fill: %s;", color));
                    }
                }
            }
        });
    }
    

    private void updatePieChart(HashMap<String, DataSet> dataRange) {
        pieChart.getData().clear();
        
        Map<String, Double> pollutantAverages = new HashMap<>();
        
        // Calculate average for each pollutant
        for (Map.Entry<String, DataSet> entry : dataRange.entrySet()) {
            String[] keyParts = entry.getKey().split("-");
            String pollutant = keyParts[1].toLowerCase();
            
            double avgValue = entry.getValue().getData().stream()
                    .mapToDouble(DataPoint::value)
                    .average()
                    .orElse(0);
            
            pollutantAverages.put(pollutant, avgValue);
        }
        
        // Add data to the pie chart
        for (Map.Entry<String, Double> entry : pollutantAverages.entrySet()) {
            String pollutant = entry.getKey();
            double value = entry.getValue();
            
            PieChart.Data data = new PieChart.Data(pollutant.toUpperCase(), value);
            pieChart.getData().add(data);
        }
        
        // Apply consistent colors
        Platform.runLater(() -> {
            int i = 0;
            for (PieChart.Data data : pieChart.getData()) {
                String pollutant = data.getName().toLowerCase();
                String color = pollutantColors.getOrDefault(pollutant.toUpperCase(), "black");
                
                data.getNode().setStyle(String.format("-fx-pie-color: %s;", color));
                i++;
            }
        });
    }

    public ChartType getCurrentChartType() {
        return currentChartType;
    }

    public Pane getChartBox() {
        return chartContainer;
    }
}