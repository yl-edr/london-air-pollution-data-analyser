import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PollutionStatistics {

    private BorderPane borderPane;
    private VBox chartBox;
    private Chart chart;

    private String fromYearSelected;
    private String toYearSelected;

    private DataAggregator dataAggregator;

    private GridPane rightBar;
    private Set<String> selectedPollutants = new HashSet<>();

    private Label titleLabel;

    private Label pollutantLabel;
    private Label fromYearLabel;
    private Label toYearLabel;
    private ComboBox<String> fromYearComboBox;
    private ComboBox<String> toYearComboBox;
    private HBox toggleButtons;
    private Label maxLabel;
    private Label maxPolValue;
    private Label maxGridCode;
    private Label maxLocValue;
    private Label minLabel;
    private Label minPolValue;
    private Label minGridCode;
    private Label minLocValue;
    
    // Chart type selection
    private Label chartTypeLabel;
    private ComboBox<String> chartTypeComboBox;

    public PollutionStatistics(DataAggregator dataAggregator) {
        this.dataAggregator = dataAggregator;
        System.out.println(dataAggregator);
        borderPane = new BorderPane();

        chartBox = new VBox();
        chart = new Chart();
        chartBox.getChildren().add(chart.getChartBox());
        
        VBox.setVgrow(chart.getChartBox(), Priority.ALWAYS);
        chartBox.setFillWidth(true);
        
        borderPane.setCenter(chartBox);

        rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setMinWidth(375);

        titleLabel = new Label("Statistics Panel");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        titleLabel.getStyleClass().add("titleLabel");

        pollutantLabel = new Label("Select Pollutant(s):");

        ToggleButton pm25Toggle = new ToggleButton("PM2.5");
        ToggleButton no2Toggle = new ToggleButton("NO2");
        ToggleButton pm10Toggle = new ToggleButton("PM10");

        pm25Toggle.setOnAction(e -> togglePollutant("pm2.5", pm25Toggle));
        no2Toggle.setOnAction(e -> togglePollutant("no2", no2Toggle));
        pm10Toggle.setOnAction(e -> togglePollutant("pm10", pm10Toggle));

        toggleButtons = new HBox(10, pm25Toggle, no2Toggle, pm10Toggle);

        fromYearLabel = new Label("From Year:");
        fromYearComboBox = new ComboBox<>();
        fromYearComboBox.setPromptText("Select Start Year");
        fromYearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");

        toYearLabel = new Label("To Year:");
        toYearComboBox = new ComboBox<>();
        toYearComboBox.setPromptText("Select End Year");
        toYearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
        
        // Chart Type Selection
        chartTypeLabel = new Label("Chart Type:");
        chartTypeComboBox = new ComboBox<>();
        chartTypeComboBox.setPromptText("Select Chart Type");
        chartTypeComboBox.getItems().addAll("Line Chart", "Bar Chart", "Area Chart", "Pie Chart");
        chartTypeComboBox.getSelectionModel().select("Line Chart"); // Default selection

        maxLabel = new Label("Highest pollution level:");
        maxPolValue = new Label(" 0.0");
        maxGridCode = new Label("Gridcode: ");
        maxLocValue = new Label(" N/A");

        minLabel = new Label("Lowest pollution level:");
        minPolValue = new Label(" 0.0");
        minGridCode = new Label("Gridcode: ");
        minLocValue = new Label(" N/A");

        fromYearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    fromYearSelected = newVal;
                    validateYearSelection(fromYearSelected, toYearSelected);
                    updateChart();
        });

        toYearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    toYearSelected = newVal;
                    validateYearSelection(fromYearSelected, toYearSelected);
                    updateChart();
        });
        
        chartTypeComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    updateChartType(newVal);
                    updateChart();
        });

        setUpSideBar();  
        borderPane.setRight(rightBar);
    }
    
    private void setUpSideBar() {        
        rightBar.add(titleLabel, 0, 0);
        rightBar.add(new Label(" "), 0, 1);
        rightBar.add(pollutantLabel, 0, 2);
        rightBar.add(toggleButtons, 0, 3);
        rightBar.add(new Label(" "), 0, 4);
        rightBar.add(chartTypeLabel, 0, 5);
        rightBar.add(chartTypeComboBox, 0, 6);
        rightBar.add(new Label(" "), 0, 7);
        rightBar.add(fromYearLabel, 0, 8);
        rightBar.add(fromYearComboBox, 0, 9);
        rightBar.add(new Label(" "), 0, 10);
        rightBar.add(toYearLabel, 0, 11);
        rightBar.add(toYearComboBox, 0, 12);
        rightBar.add(new Label(" "), 0, 13);
        rightBar.add(new Label(" "), 0, 14); 
        rightBar.add(maxLabel, 0, 15);
        rightBar.add(maxPolValue, 1, 15);
        rightBar.add(maxGridCode, 0, 16);
        rightBar.add(maxLocValue, 1, 16);
        rightBar.add(new Label(" "), 0, 17);
        rightBar.add(minLabel, 0, 18);
        rightBar.add(minPolValue, 1, 18);
        rightBar.add(minGridCode, 0, 19);
        rightBar.add(minLocValue, 1, 19);
    }
    
    private void updateChartType(String chartTypeName) {
        Chart.ChartType chartType;
        switch (chartTypeName) {
            case "Bar Chart":
                chartType = Chart.ChartType.BAR;
                break;
            case "Area Chart":
                chartType = Chart.ChartType.AREA;
                break;
            case "Pie Chart":
                chartType = Chart.ChartType.PIE;
                break;
            default:
                chartType = Chart.ChartType.LINE;
                break;
        }
        chart.setChartType(chartType);
    }

    private void validateYearSelection(String fromYear, String toYear) {
        if (fromYear != null && toYear != null) {
            int from = Integer.parseInt(fromYear);
            int to = Integer.parseInt(toYear);

            if (to < from) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Year Selection");
                alert.setHeaderText("Invalid Range");
                alert.setContentText("The 'To Year' cannot be earlier than the 'From Year'!");
                alert.showAndWait();
            }
        }
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    private void updateChart() {
        if (fromYearSelected == null || toYearSelected == null || selectedPollutants.isEmpty()) {
            // Clear chart data
            HashMap<String, DataSet> emptyData = new HashMap<>();
            chart.updateChart(emptyData);
            return;
        }

        int startYear = Integer.parseInt(fromYearSelected);
        int endYear = Integer.parseInt(toYearSelected);
        HashMap<String, DataSet> data = new HashMap<>();
        double maxPolTempValue = Double.MIN_VALUE;
        double minPolTempValue = Double.MAX_VALUE;
        String maxLocTempValue = "N/A";
        String minLocTempValue = "N/A";
    
        for (String pollutant : selectedPollutants) {
            HashMap<String, DataSet> pollutantData = dataSetRange(pollutant, startYear, endYear);
            data.putAll(pollutantData);
    
            for (DataSet dataSet : pollutantData.values()) {
                for (DataPoint dataPoint : dataSet.getData()) {
                    double value = dataPoint.value();
                    int gridCode = dataPoint.gridCode();
                    
                    if (value > maxPolTempValue) {
                        maxPolTempValue = value;
                        maxLocTempValue = ((Integer) gridCode).toString();
                    }
                    if (value < minPolTempValue) {
                        minPolTempValue = value;
                        minLocTempValue = ((Integer) gridCode).toString();
                    }
                }
            }
        }
        
        chart.updateChart(data);
        maxPolValue.setText("       " + maxPolTempValue + " µg/m³");
        minPolValue.setText("       " + minPolTempValue + " µg/m³");
        maxLocValue.setText("       " + maxLocTempValue);
        minLocValue.setText("       " + minLocTempValue);
    }

    private void togglePollutant(String pollutant, ToggleButton button) {
        if (button.isSelected()) {
            selectedPollutants.add(pollutant);
        } else {
            selectedPollutants.remove(pollutant);
        }
        updateChart();
    }

    private HashMap<String, DataSet> dataSetRange(String pollutant, int startYear, int endYear){
        HashMap<String, DataSet> dataRange = new HashMap<>();
        for(int i = startYear; i <= endYear; i++){
            dataRange.put(i + "-" + pollutant, dataAggregator.getCityDataSet("London", Integer.toString(i), pollutant));
        }
        return dataRange;
    }
}