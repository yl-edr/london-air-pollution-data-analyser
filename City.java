import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

/**
 * The City abstract class represents a city with an associated map and environmental data.
 * It provides a graphical user interface to display the city map, allow pollutant and year
 * selections, and present data statistics based on user interaction.
 *
 * The class handles creating the view, tracking mouse events, updating overlays, and displaying
 * detailed information about selected data points.
 *
 * @author Nicolás Alcalá Olea, Anton Davidouski, Rom Steinberg, Yaal Edrey Gatignol
 */

public abstract class City {

    private DataAggregator dataAggregator;
    private DataSet selectedDataSet;
    private MapImage map;
    private Image mapImage;
    private ImageView mapView;
    private AnchorPane anchorPane;
    private double mapImageAspectRatio;
    private Label dataPointValue;
    private Label gridCodeValue;
    private Label xValue;
    private Label yValue;
    private String pollutantSelected;
    private String yearSelected;
    private BorderPane borderPane;
    private String name;
    private int[] bounds;
    private Label lowLabel;
    private Label highLabel;

    private int mouseX;
    private int mouseY;

    /**
     * Constructs a new City instance with the specified city name, bounds, and a
     * data aggregator for retrieving a data set of the cities pollution.
     *
     * @param cityName       the name of the city
     * @param bounds         an array representing the bounds of the city
     * @param dataAggregator the data aggregator instance used to retrieve a data set of the cities pollution
     */

    public City(String cityName, int[] bounds, DataAggregator dataAggregator) {
        this.dataAggregator = dataAggregator;
        this.name = cityName;
        this.bounds = bounds;
        create(name);
        trackMouseLocation();
    }

    /**
     * Initializes and creates the city tab view, including the map image, control panels,
     * and UI components for pollutant and year selection.
     *
     * @param name the name of the city to be displayed
     */

    public void create(String name) {

        map = new MapImage(name,"resources/" + name + ".png");
        mapImage = map.getImage();
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setFitWidth(500);
        mapImageAspectRatio = mapImage.getWidth() / mapImage.getHeight();
        mapView.getStyleClass().add("mapImage");


        anchorPane = new AnchorPane();
        anchorPane.getChildren().add(mapView);
        anchorPane.setMinWidth(500);
        anchorPane.setMinHeight(300);
        mapView.fitWidthProperty().bind(anchorPane.widthProperty());
        mapView.fitHeightProperty().bind(anchorPane.heightProperty());
        anchorPane.getStyleClass().add("anchorPane");

        borderPane = new BorderPane();
        borderPane.setCenter(anchorPane);
        BorderPane.setMargin(anchorPane, new Insets(5,0,5,5));
        borderPane.getStyleClass().add("borderPane");

        GridPane rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setPrefWidth(264);
        rightBar.setMinWidth(150);
        rightBar.setMaxWidth(300);
        rightBar.getStyleClass().add("rightBar");

        Label pollutantLabel = new Label("Choose a pollutant:");
        pollutantLabel.getStyleClass().add("pollutantLabel");
        ComboBox<String> pollutantComboBox = new ComboBox<>();
        pollutantComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pollutantSelected = newValue;
                    updateColourMap();
                });
        pollutantComboBox.setPromptText("Pollutant");
        pollutantComboBox.getItems().addAll("pm2.5", "no2", "pm10");
        pollutantComboBox.getStyleClass().add("pollutantComboBox");

        Label yearLabel = new Label("Choose a year:");
        yearLabel.getStyleClass().add("yearLabel");
        ComboBox<String> yearComboBox = new ComboBox<>();
        yearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    yearSelected = newValue;
                    updateColourMap();
                });
        yearComboBox.setPromptText("Year");
        yearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");
        yearComboBox.getStyleClass().add("yearComboBox");

        Button predictionButton = new Button("Predict");
        predictionButton.setOnAction(event -> {
            if (!yearComboBox.getItems().contains("2024")) {
                yearComboBox.getItems().add("2024");
                new Prediction(dataAggregator);
            }
        });
        predictionButton.getStyleClass().add("predictButton");

        Label dataPointLabel = new Label("Value: ");
        dataPointValue = new Label("Select a year and pollutant");
        dataPointLabel.getStyleClass().add("dataPointLabel");
        dataPointValue.getStyleClass().add("dataPointValue");

        Label gridCodeLabel = new Label("Grid Code: ");
        gridCodeValue = new Label("Select a year and pollutant");
        gridCodeLabel.getStyleClass().add("gridCodeLabel");
        gridCodeValue.getStyleClass().add("gridCodeValue");

        xValue = new Label();
        xValue.getStyleClass().add("xValue");

        yValue = new Label();
        yValue.getStyleClass().add("yValue");

        xValue.setVisible(false);
        yValue.setVisible(false);

        mapView.setOnMouseMoved(event -> {
            xValue.setVisible(true);
            yValue.setVisible(true);
            xValue.setText("X: " + (int) event.getX());
            yValue.setText("Y: " + (int) event.getY());
            mouseX = (int) event.getX();
            mouseY = (int) event.getY();
            updateStats();
        });

        rightBar.add(pollutantLabel, 0, 0);
        rightBar.add(pollutantComboBox, 0, 1);
        rightBar.add(yearLabel, 0, 2);
        rightBar.add(yearComboBox, 0, 3);
        rightBar.add(predictionButton, 0, 4);
        rightBar.add(dataPointLabel, 0, 5);
        rightBar.add(dataPointValue, 0, 6);
        rightBar.add(gridCodeLabel, 0, 7);
        rightBar.add(gridCodeValue, 0, 8);
        rightBar.add(xValue, 0, 9);
        rightBar.add(yValue, 0, 10);

        GridPane.setMargin(yearLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(yearComboBox, new Insets(0, 0, 10, 0));
        GridPane.setMargin(predictionButton, new Insets(10, 0, 10, 0));
        GridPane.setMargin(dataPointLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(dataPointValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(gridCodeLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(gridCodeValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(xValue, new Insets(20, 0, 10, 0));
        GridPane.setMargin(yValue, new Insets(5, 0, 10, 0));

        borderPane.setRight(rightBar);

        HBox aqiBarContainer = new HBox(10);
        aqiBarContainer.setAlignment(Pos.CENTER);
        aqiBarContainer.setPrefHeight(50);
        aqiBarContainer.setMinHeight(30);
        aqiBarContainer.setMaxHeight(80);
        //aqiBarContainer.setPadding(new Insets(10));
        //BorderPane.setMargin(aqiBarContainer, new Insets(10));
        aqiBarContainer.getStyleClass().add("aqiBarContainer");

        lowLabel = new Label("GOOD");
        lowLabel.getStyleClass().add("lowLabel");

        highLabel = new Label("POOR");
        highLabel.getStyleClass().add("highLabel");

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

    /**
     * Updates the displayed statistics based on the current mouse position on the map.
     * It calculates the corresponding coordinates in the dataset using the current map view
     * dimensions and updates the UI labels with the nearest data point's value and grid code.
     */

    public void updateStats(){
        if (selectedDataSet == null) {
            return;
        }
        int[] imageDimensions = convertMapViewDimensionsToImageDimensions((int) mapView.getFitWidth(), (int) mapView.getFitHeight());
        int imageWidth = imageDimensions[0];
        int imageHeight = imageDimensions[1];
        int x = (int) ((mouseX / (double) imageWidth) * (bounds[1] - bounds[0]) + bounds[0]);
        int y = (int) (bounds[3] - ((mouseY / (double) imageHeight) * (bounds[3] - bounds[2])));
        DataPoint nearestDataPoint = selectedDataSet.findNearestDataPoint(x, y);
        dataPointValue.setText(nearestDataPoint.value() + " " + selectedDataSet.getUnits());
        gridCodeValue.setText(String.valueOf(nearestDataPoint.gridCode()));
    }

    /**
     * Returns the primary pane containing the city map and UI controls.
     *
     * @return the BorderPane representing the city's main view
     */

    public BorderPane getPane() {
        return borderPane;
    }

    /**
     * Updates the color overlay on the city map based on the selected pollutant and year.
     * It retrieves the corresponding dataset from the data aggregator, processes each data point
     * to apply a color mapping, and then updates the map view with a blurred overlay.
     */

    public void updateColourMap(){
        if (pollutantSelected == null || yearSelected == null) {
            return;
        }
        selectedDataSet = dataAggregator.getCityDataSet(name,yearSelected, pollutantSelected);
        System.out.println(name);
        System.out.println(yearSelected);
        System.out.println(pollutantSelected);
        lowLabel.setText(String.format("%.1f", selectedDataSet.getMin())+" MIN");
        highLabel.setText(String.format("%.1f", selectedDataSet.getMax())+" MAX");

        map.resetOverlay();
        for (DataPoint dataPoint : selectedDataSet.getData()) {
            if (dataPoint.value() > 0) {
                map.processDataPoint(dataPoint, selectedDataSet.getMin(), selectedDataSet.getMax(),bounds[4]);
            }
        }
        map.applyBlur(60);
        Image mapImage = map.getCombined();
        mapView.setImage(mapImage);
    }

    /**
     * Converts the given dimensions of the map view to the corresponding dimensions of the underlying map image.
     *
     * @param x the width of the map view
     * @param y the height of the map view
     * @return an array of two integers where index 0 is the calculated image width and index 1 is the image height
     */

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

    /**
     * Sets up mouse tracking on the map view. When a mouse click is detected,
     * it calculates the corresponding coordinates in the dataset and displays detailed
     * information about the nearest data point.
     */

    private void trackMouseLocation() {
        mapView.setOnMouseClicked(event -> {
            if (selectedDataSet != null) {
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

    /**
     * Displays an informational alert dialog with details about the specified data point.
     *
     * @param dataPoint the data point whose information is to be displayed
     */

    private void showDataPointInfo(DataPoint dataPoint) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data Point Information");
        alert.setHeaderText(null);
        alert.setContentText("Grid Code: " + dataPoint.gridCode() + "\nX: " + dataPoint.x() + "\nY: " + dataPoint.y() + "\nValue: " + dataPoint.value());
        alert.showAndWait();
    }

    /**
     * Retrieves the data aggregator associated with this city.
     *
     * @return the data aggregator used for accessing environmental data
     */

    public DataAggregator getDataAggregator() {
        return dataAggregator;
    }

    

}
