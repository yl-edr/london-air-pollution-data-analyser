import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PollutionStatistics {

    private MapImage map;
    private ImageView mapView;

    private AnchorPane anchorPane;
    private Image mapImage;
    private BorderPane borderPane;
    private VBox chartPane;
    private Chart chart;

    private String fromYearSelected;
    private String toYearSelected;

    private DataAggregator dataAggregator;

    private GridPane rightBar;
    private VBox centerVBox;
    private Button toggleButton;
    private boolean isMapVisible;
    private Set<String> selectedPollutants = new HashSet<>();

    private Label titleLabel;

    // Chart fields    private GridPane rightBar;
    private Label pollutantLabel;
    private Label fromYearLabel;
    private Label toYearLabel;
    private ComboBox<String> fromYearComboBox;
    private ComboBox<String> toYearComboBox;
    private HBox toggleButtons;
    private Label maxLabel;
    private Label maxValue;
    private Label maxGridCode;
    private Label maxGridCodeValue;
    private Label minLabel;
    private Label minValue;
    private Label minGridCode;
    private Label minGridCodeValue;

    // Map fields
    private HBox radioButtons;
    private ToggleGroup pollutantGroup;



    public PollutionStatistics(DataAggregator dataAggregator) {
        this.dataAggregator = dataAggregator;
        System.out.println(dataAggregator);
        borderPane = new BorderPane();

        map = new MapImage("London", "resources/London.png");
        mapImage = map.getImage();
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setFitWidth(900);

        anchorPane = new AnchorPane();
        anchorPane.getChildren().add(mapView);
        anchorPane.setMinWidth(500);
        anchorPane.setMinHeight(300);
        mapView.fitWidthProperty().bind(anchorPane.widthProperty());
        mapView.fitHeightProperty().bind(anchorPane.heightProperty());
        
        chartPane = new VBox();
        chart = new Chart();
        chartPane.getChildren().add(chart.getChart());
        
        // Toggle Button
        toggleButton = new Button("Switch to Chart");
        toggleButton.setOnAction(e -> toggleView());

        // Center container
        centerVBox = new VBox();
        centerVBox.setFillWidth(true);
        centerVBox.getChildren().addAll(toggleButton, anchorPane);
        VBox.setVgrow(anchorPane, Priority.ALWAYS);
        VBox.setVgrow(chartPane, Priority.ALWAYS);

        borderPane.setCenter(centerVBox);

        rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setMinWidth(375);

        titleLabel = new Label("Statistics Panel - Map View");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        titleLabel.getStyleClass().add("titleLabel");
        rightBar.add(titleLabel, 0, 0);

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

        maxLabel = new Label("Highest pollution level:");
        maxValue = new Label(" 0.0");
        maxGridCode = new Label("Gridcode: ");
        maxGridCodeValue = new Label(" N/A");

        minLabel = new Label("Lowest pollution level:");
        minValue = new Label(" 0.0");
        minGridCode = new Label("Gridcode: ");
        minGridCodeValue = new Label(" N/A");

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


        // Map
        pollutantGroup = new ToggleGroup();
        RadioButton pm25Radio = new RadioButton("PM2.5");
        pm25Radio.setToggleGroup(pollutantGroup);
        pm25Radio.setOnAction(e -> togglePollutant("pm2.5", pm25Radio));
        RadioButton no2Radio = new RadioButton("NO2");
        no2Radio.setToggleGroup(pollutantGroup);
        no2Radio.setOnAction(e -> togglePollutant("no2", no2Radio));
        RadioButton pm10Radio = new RadioButton("PM10");
        pm10Radio.setToggleGroup(pollutantGroup);
        pm10Radio.setOnAction(e -> togglePollutant("pm10", pm10Radio));
        radioButtons = new HBox(10, pm25Radio, no2Radio, pm10Radio);

        

        borderPane.setRight(rightBar);

        isMapVisible = true;
        toggleView();
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
            chart.getChart().getData().clear();
            return;
        }

        int startYear = Integer.parseInt(fromYearSelected);
        int endYear = Integer.parseInt(toYearSelected);
        HashMap<String, DataSet> data = new HashMap<>();
        double maxPollutionLevel = Double.MIN_VALUE;
        double minPollutionLevel = Double.MAX_VALUE;
        String maxLocTempValue = "N/A";
        String minLocTempValue = "N/A";
    
        for (String pollutant : selectedPollutants) {
            HashMap<String, DataSet> pollutantData = dataSetRange(pollutant, startYear, endYear);
            data.putAll(pollutantData);
    
            for (DataSet dataSet : pollutantData.values()) {
                for (DataPoint dataPoint : dataSet.getData()) {
                    double value = dataPoint.value();
                    int gridCode = dataPoint.gridCode();
                    
                    if (value > maxPollutionLevel) {
                        maxPollutionLevel = value;
                        maxLocTempValue = ((Integer) gridCode).toString();
                    }
                    if (value < minPollutionLevel) {
                        minPollutionLevel = value;
                        minLocTempValue = ((Integer) gridCode).toString();
                    }
                }
            }
        }
        
        chart.updateChart(data);
        maxValue.setText(maxPollutionLevel + " µg/m³");
        minValue.setText(minPollutionLevel + " µg/m³");
        maxGridCodeValue.setText(" " + maxLocTempValue);
        minGridCodeValue.setText(" " + minLocTempValue);
    }

    private void toggleView() {
        centerVBox.getChildren().remove(isMapVisible ? anchorPane : chartPane);
        rightBar.getChildren().clear();
        isMapVisible = !isMapVisible;
        centerVBox.getChildren().add(isMapVisible ? anchorPane : chartPane);
        toggleButton.setText(isMapVisible ? "Switch to Chart" : "Switch to Map");
        titleLabel.setText(isMapVisible ? "Statistics Panel - Map View" : "Statistics Panel - Chart View");
        if (isMapVisible) {
            rightBar.add(titleLabel, 0, 0);
            rightBar.add(new Label(" "), 0, 1);
            rightBar.add(pollutantLabel, 0, 2);
            rightBar.add(radioButtons, 0, 3);
            rightBar.add(new Label(" "), 0, 4);
            rightBar.add(fromYearLabel, 0, 5);
            rightBar.add(fromYearComboBox, 0, 6);
            rightBar.add(new Label(" "), 0, 7);
            rightBar.add(new Label(" "), 0, 8);
        }
        else {
            rightBar.add(titleLabel, 0, 0);
            rightBar.add(new Label(" "), 0, 1);
            rightBar.add(pollutantLabel, 0, 2);
            rightBar.add(toggleButtons, 0, 3);
            rightBar.add(new Label(" "), 0, 4);
            rightBar.add(fromYearLabel, 0, 5);
            rightBar.add(fromYearComboBox, 0, 6);
            rightBar.add(new Label(" "), 0, 7);
            rightBar.add(toYearLabel, 0, 8);
            rightBar.add(toYearComboBox, 0, 9);
            rightBar.add(new Label(" "), 0, 10);
            rightBar.add(new Label(" "), 0, 11); 
            rightBar.add(new Label(" "), 0, 12); 
            rightBar.add(maxLabel, 0, 13);
            rightBar.add(maxValue, 1, 13);
            rightBar.add(maxGridCode, 0, 14);
            rightBar.add(maxGridCodeValue, 1, 14);
            rightBar.add(new Label(" "), 0, 15);
            rightBar.add(minLabel, 0, 16);
            rightBar.add(minValue, 1, 16);
            rightBar.add(minGridCode, 0, 17);
            rightBar.add(minGridCodeValue, 1, 17);
        }
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