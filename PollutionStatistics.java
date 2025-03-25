import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PollutionStatistics {

    private MapImage map;
    private Image mapImage;
    private ImageView mapView;

    private VBox mapPane;
    private BorderPane borderPane;
    private VBox chartPane;
    private Chart chart;

    private String pollutantSelected;

    private Label maxValue;

    private String fromYearSelected;
    private String toYearSelected;

    private DataAggregator dataAggregator;

    private VBox centerVBox;
    private Button toggleButton;
    private boolean isMapVisible = true;
    private Set<String> selectedPollutants = new HashSet<>();

    public PollutionStatistics(DataAggregator dataAggregator) {
        this.dataAggregator = dataAggregator;
        System.out.println(dataAggregator);
        borderPane = new BorderPane();

        // Initialize map
        map = new MapImage("London", "resources/London.png");
        mapView = new ImageView(map.getImage());
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setFitWidth(900);
        
        AnchorPane mapContainer = new AnchorPane(mapView);
        AnchorPane.setTopAnchor(mapView, 0.0);
        AnchorPane.setBottomAnchor(mapView, 0.0);
        AnchorPane.setLeftAnchor(mapView, 0.0);
        AnchorPane.setRightAnchor(mapView, 0.0);

        mapPane = new VBox(mapContainer);
        mapPane.setMinHeight(375);
        VBox.setVgrow(mapContainer, Priority.ALWAYS);
        
        // Initialize chart
        chartPane = new VBox();
        chart = new Chart();
        chartPane.getChildren().add(chart.getChart());
        chartPane.setPrefHeight(2000);

        // Toggle Button
        toggleButton = new Button("Switch to Chart");
        toggleButton.setOnAction(e -> toggleView());

        // Center container
        centerVBox = new VBox();
        centerVBox.setFillWidth(true);
        centerVBox.getChildren().addAll(toggleButton, mapPane);
        VBox.setVgrow(mapPane, Priority.ALWAYS);

        borderPane.setCenter(centerVBox);

        GridPane rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setMinWidth(375);

        Label titleLabel = new Label("Statistics Panel");
        titleLabel.getStyleClass().add("titleLabel");

        Label pollutantLabel = new Label("Select Pollutants:");

        ToggleButton pm25Button = new ToggleButton("PM2.5");
        ToggleButton no2Button = new ToggleButton("NO2");
        ToggleButton pm10Button = new ToggleButton("PM10");

        pm25Button.setOnAction(e -> togglePollutant("pm2.5", pm25Button));
        no2Button.setOnAction(e -> togglePollutant("no2", no2Button));
        pm10Button.setOnAction(e -> togglePollutant("pm10", pm10Button));

        HBox toggleButtons = new HBox(10, pm25Button, no2Button, pm10Button);

        Label fromYearLabel = new Label("From Year:");
        ComboBox<String> fromYearComboBox = new ComboBox<>();
        fromYearComboBox.setPromptText("Select Start Year");
        fromYearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");

        Label toYearLabel = new Label("To Year:");
        ComboBox<String> toYearComboBox = new ComboBox<>();
        toYearComboBox.setPromptText("Select End Year");
        toYearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");

        Label maxLabel = new Label("Highest pollution level:");
        maxValue = new Label("  0.0");

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

        rightBar.add(titleLabel, 0, 0);
        rightBar.add(pollutantLabel, 0, 1);
        rightBar.add(toggleButtons, 0, 2);
        rightBar.add(fromYearLabel, 0, 3);
        rightBar.add(fromYearComboBox, 0, 4);
        rightBar.add(toYearLabel, 0, 5);
        rightBar.add(toYearComboBox, 0, 6);
        rightBar.add(maxLabel, 0, 7);
        rightBar.add(maxValue, 1, 7);

        //VBox.setVgrow(maxValue, Priority.ALWAYS);

        GridPane.setMargin(maxLabel, new Insets(210, 0, 0, 0));
        GridPane.setMargin(maxValue, new Insets(210, 0, 0, 0));

        borderPane.setRight(rightBar);
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
        if (fromYearSelected == null || toYearSelected == null || selectedPollutants.isEmpty()) return;
        
        int startYear = Integer.parseInt(fromYearSelected);
        int endYear = Integer.parseInt(toYearSelected);
        
        HashMap<String, DataSet> data = new HashMap<>();
        double maxPollutionLevel = Double.MIN_VALUE; // Initialize to the smallest possible value
    
        for (String pollutant : selectedPollutants) {
            HashMap<String, DataSet> pollutantData = dataSetRange(pollutant, startYear, endYear);
            data.putAll(pollutantData);
            
            // Calculate the maximum pollution level for the current pollutant
            for (DataSet dataSet : pollutantData.values()) {
                for (DataPoint dataPoint : dataSet.getData()) {
                    double value = dataPoint.value();
                    if (value > maxPollutionLevel) {
                        maxPollutionLevel = value;
                    }
                }
            }
        }
        
        chart.updateChart(data);
        maxValue.setText(String.valueOf(maxPollutionLevel) + " µg/m³");
    }

    private void toggleView() {
        centerVBox.getChildren().remove(isMapVisible ? mapPane : chartPane);
        isMapVisible = !isMapVisible;
        centerVBox.getChildren().add(isMapVisible ? mapPane : chartPane);
        toggleButton.setText(isMapVisible ? "Switch to Chart" : "Switch to Map");
    }

    private void togglePollutant(String pollutant, ToggleButton button) {
        if (selectedPollutants.contains(pollutant)) {
            selectedPollutants.remove(pollutant);
        } else {
            selectedPollutants.add(pollutant);
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