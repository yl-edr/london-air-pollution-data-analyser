import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class City {

    private DataAggregator dataAggregator;
    private DataSet selectedDataSet;
    private MapImage map;
    private Image mapImage;
    private ImageView mapView;
    private AnchorPane anchorPane;
    private GridPane rightBar;
    private double mapImageAspectRatio;
    private Label dataPointValue;
    private Label gridCodeValue;
    private Label xValue;
    private Label yValue;
    private String pollutantSelected;
    private ComboBox<String> cityComboBox;
    private String yearSelected;
    //private String citySelected;
    private BorderPane borderPane;
    protected String name;
    private static final HashMap<String, int[]> CITY_BOUNDARIES = new HashMap<>();

    static {
        // Add boundaries for different cities (adjust values as needed)
        CITY_BOUNDARIES.put("London", new int[]{510394, 554000, 168000, 194000, 1});
        CITY_BOUNDARIES.put("Manchester", new int[]{376000, 390901, 393400, 401667, 3});
        CITY_BOUNDARIES.put("Edinburgh", new int[]{317339, 331640, 668176, 676443, 3});
        CITY_BOUNDARIES.put("Birmingham", new int[]{401000, 415930, 282200, 290530, 3});
        CITY_BOUNDARIES.put("Leeds", new int[]{421070, 436570, 430350, 438580, 3});
    }

    private int mouseX;
    private int mouseY;

    public City(String cityName, DataAggregator dataAggregator) {
        this.dataAggregator = dataAggregator;
        this.name = cityName;
        create(name);
        trackMouseLocation();
    }

    public void create(String name) {
        map = new MapImage(name,"resources/"+name+".png");
        mapImage = map.getImage();
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setFitWidth(500);
        mapImageAspectRatio = mapImage.getWidth() / mapImage.getHeight();

        anchorPane = new AnchorPane();
        anchorPane.getChildren().add(mapView);
        anchorPane.setMinWidth(500);
        anchorPane.setMinHeight(300);
        mapView.fitWidthProperty().bind(anchorPane.widthProperty());
        mapView.fitHeightProperty().bind(anchorPane.heightProperty());

        borderPane = new BorderPane();
        borderPane.setCenter(anchorPane);

        rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setPrefWidth(250);
        rightBar.setMinWidth(150);
        rightBar.setMaxWidth(300);
        
        if(!name.equals("London")){
            createCitySelector();
        }

        Label pollutantLabel = new Label("Choose a pollutant:");
        ComboBox<String> pollutantComboBox = new ComboBox<>();
        pollutantComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pollutantSelected = newValue;
                    pollutantComboBox.setPromptText(pollutantSelected);
                    updateColourMap();
                });
        pollutantComboBox.setPromptText("Pollutant");
        pollutantComboBox.getItems().addAll("pm2.5", "no2", "pm10");

        Label yearLabel = new Label("Choose a year:");
        ComboBox<String> yearComboBox = new ComboBox<>();
        yearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    yearSelected = newValue;
                    yearComboBox.setPromptText(yearSelected);
                    updateColourMap();
                });
        yearComboBox.setPromptText("Year");
        yearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");


        Label dataPointLabel = new Label("Value: ");
        dataPointValue = new Label("select a data point");

        Label gridCodeLabel = new Label("Grid Code: ");
        gridCodeValue = new Label("select a data point");

        Label xLabel = new Label("X: ");
        xValue = new Label("select a data point");

        Label yLabel = new Label("Y: ");
        yValue = new Label("select a data point");

        mapView.setOnMouseMoved(event -> {
            xValue.setText("X: " + (int) event.getX());
            yValue.setText("Y: " + (int) event.getY());
            mouseX = (int) event.getX();
            mouseY = (int) event.getY();
            updateStats();
        });

        rightBar.add(pollutantLabel, 0, 2);
        rightBar.add(pollutantComboBox, 0, 3);
        rightBar.add(yearLabel, 0, 4);
        rightBar.add(yearComboBox, 0, 5);
        rightBar.add(dataPointLabel, 0, 7);
        rightBar.add(dataPointValue, 0, 8);
        rightBar.add(gridCodeLabel, 0, 9);
        rightBar.add(gridCodeValue, 0, 10);
        rightBar.add(xLabel, 0, 11);
        rightBar.add(xValue, 0, 12);
        rightBar.add(yLabel, 0, 13);
        rightBar.add(yValue, 0, 14);

        GridPane.setMargin(yearLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(yearComboBox, new Insets(0, 0, 10, 0));
        GridPane.setMargin(dataPointLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(dataPointValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(gridCodeLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(gridCodeValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(xLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(xValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(yLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(yValue, new Insets(0, 0, 10, 0));

        borderPane.setRight(rightBar);

        HBox aqiBarContainer = new HBox(10);
        aqiBarContainer.setAlignment(Pos.CENTER);
        aqiBarContainer.setPrefHeight(50);
        aqiBarContainer.setMinHeight(30);
        aqiBarContainer.setMaxHeight(80);
        aqiBarContainer.setPadding(new Insets(10));
        BorderPane.setMargin(aqiBarContainer, new Insets(10));

        Label lowLabel = new Label("GOOD");
        Label highLabel = new Label("POOR");
        lowLabel.setMinWidth(Region.USE_PREF_SIZE);
        highLabel.setMinWidth(Region.USE_PREF_SIZE);

        Region aqiBar = new Region();
        aqiBar.setPrefHeight(20);
        aqiBar.setMaxWidth(Double.MAX_VALUE);
        aqiBar.getStyleClass().add("aqiBar");

        StackPane aqiStack = new StackPane();
        aqiStack.setAlignment(Pos.CENTER);
        HBox.setHgrow(aqiStack, Priority.ALWAYS);
        aqiBar.prefWidthProperty().bind(aqiStack.widthProperty());
        aqiStack.getChildren().addAll(aqiBar);

        aqiBarContainer.getChildren().addAll(lowLabel, aqiStack, highLabel);
        borderPane.setBottom(aqiBarContainer);
    }

    public GridPane getRightBar() {
        return rightBar;
    }

    public void updateStats(){
        if (selectedDataSet == null) {
            return;
        }
        int[] bounds = CITY_BOUNDARIES.get(name);
        int[] imageDimensions = convertMapViewDimensionsToImageDimensions((int) mapView.getFitWidth(), (int) mapView.getFitHeight());
        int imageWidth = imageDimensions[0];
        int imageHeight = imageDimensions[1];
        int x = (int) ((mouseX / (double) imageWidth) * (bounds[1] - bounds[0]) + bounds[0]);
        int y = (int) (bounds[2] - ((mouseY / (double) imageHeight) * (bounds[2] - bounds[3])));
        DataPoint nearestDataPoint = selectedDataSet.findNearestDataPoint(x, y);
        dataPointValue.setText(nearestDataPoint.value() + " " + selectedDataSet.getUnits());
        gridCodeValue.setText(String.valueOf(nearestDataPoint.gridCode()));
    }

    public BorderPane getPane() {
        return borderPane;
    }


    public void updateColourMap(){
        if (pollutantSelected == null || yearSelected == null) {
            return;
        }
        selectedDataSet = dataAggregator.getCityDataSet(name,yearSelected, pollutantSelected);
        System.out.println(name + " " + yearSelected + " " + pollutantSelected);

        map.resetOverlay();
        for (DataPoint dataPoint : selectedDataSet.getData()) {
            if (dataPoint.value() > 0) {
                map.processDataPoint(dataPoint, selectedDataSet.getMin(), selectedDataSet.getMax(),CITY_BOUNDARIES.get(name)[4]);
            }
        }
        map.applyBlur(60);
        Image mapImage = map.getCombined();
        mapView.setImage(mapImage);
    }

    public static HashMap<String, int[]> getCitiesBoundaries() {
        return CITY_BOUNDARIES;
    }

    public int[] convertMapViewDimensionsToImageDimensions(int x, int y) {
        double providedAspectRatio = (double) x / y;
        int[] dimensions = new int[2];
        if (providedAspectRatio > mapImageAspectRatio) {
            dimensions[0] = (int) (y * mapImageAspectRatio);
            dimensions[1] = y;
        } else {
            dimensions[0] = x;
            dimensions[1] = (int) (x / mapImageAspectRatio);
        }
        return dimensions;
    }

    private void trackMouseLocation() {
        mapView.setOnMouseClicked(event -> {
            if (selectedDataSet != null) {
                int[] bounds = CITY_BOUNDARIES.get(name);
                int[] imageDimensions = convertMapViewDimensionsToImageDimensions((int) mapView.getFitWidth(), (int) mapView.getFitHeight());
                int imageWidth = imageDimensions[0];
                int imageHeight = imageDimensions[1];
                int x = (int) ((mouseX / (double) imageWidth) * (bounds[1] - bounds[0]) + bounds[0]);
                int y = (int) (bounds[2] - ((mouseY / (double) imageHeight) * (bounds[2] - bounds[3])));
                DataPoint nearestDataPoint = selectedDataSet.findNearestDataPoint(x, y);
                showDataPointInfo(nearestDataPoint);
            }
        });
    }

    public void createCitySelector() {
        Label cityLabel = new Label("Choose a city:");
        cityComboBox = new ComboBox<>();
        cityComboBox.setPromptText(name);
        
        Set<String> cities = new HashSet<>(CITY_BOUNDARIES.keySet());
        cities.remove("London"); // Remove London from the list
        cities.remove(name); // Remove the current city from the list
        cityComboBox.getItems().addAll(cities);
        cityComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    updateCity(newValue);
                });
        GridPane.setMargin(cityComboBox, new Insets(0, 0, 10, 0));
        getRightBar().add(cityLabel, 0, 0);
        getRightBar().add(cityComboBox, 0, 1);
    }

    private void updateCity(String cityName) {
        switch (cityName) {
            case "Manchester":
                this.name = "Manchester";  // Update city name
                break;
            case "Edinburgh":
                this.name = "Edinburgh";
                break;
            case "Birmingham":
                this.name = "Birmingham";
                break;
            case "Leeds":
                this.name = "Leeds";
                break;
        
            default:
                break;
        }
        create(name);
        trackMouseLocation();
        AppWindow.setUKCities(this);
        
    }

    private void showDataPointInfo(DataPoint dataPoint) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data Point Information");
        alert.setHeaderText(null);
        alert.setContentText("Grid Code: " + dataPoint.gridCode() + "\nX: " + dataPoint.x() + "\nY: " + dataPoint.y() + "\nValue: " + dataPoint.value());
        alert.showAndWait();
    }

    public DataAggregator getDataAggregator() {
        return dataAggregator;
    }

    

}
