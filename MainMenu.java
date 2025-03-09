import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainMenu extends Application {

    // min and max Y are swapped because the top left of the image is actually the max Y
    private static final int MIN_X = 510394;
    private static final int MAX_X = 553297;
    private static final int MIN_Y = 193305;
    private static final int MAX_Y = 168504;

    private BorderPane root;
    private TabPane tabPane;

    private Tab homeTab;
    private Tab mapViewTab;
    private Tab statsTab;
    private Tab gridDataTab;

    private String pollutantSelected;
    private String yearSelected;

    private DataAggregator dataAggregator;
    private DataSet selectedDataSet;
    private MapImage map;
    private Image mapImage;
    private ImageView mapView;
    private AnchorPane anchorPane;

    private Label dataPointValue;
    private Label gridCodeValue;
    private Label xValue;
    private Label yValue;

    private int mouseX;
    private int mouseY;

    private PollutionStatistics pollutionStatistics;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        createTabPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1150, 650);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("London Air Pollution Data Viewer");
        stage.setScene(scene);
        stage.show();

        Alert startAlert = new Alert(Alert.AlertType.INFORMATION);
        startAlert.setTitle("Loading Data");
        startAlert.setHeaderText("Please wait...");
        startAlert.setContentText("Data is being loaded. " + "This popup will close when the data is loaded.");
        startAlert.initOwner(stage);
        startAlert.show();

        Task<Void> dataLoadingTask = new Task<>() {
            @Override
            protected Void call() {
                dataAggregator = new DataAggregator();
                dataAggregator.processDirectory("UKAirPollutionData/NO2/");
                dataAggregator.processDirectory("UKAirPollutionData/pm10/");
                dataAggregator.processDirectory("UKAirPollutionData/pm2.5/");
                return null;
            }

            protected void succeeded(){
                startAlert.close();
            }
        };

        new Thread(dataLoadingTask).start();
    }

    private void createTabPane() {
        tabPane = new TabPane();

        homeTab = new Tab("Welcome");
        homeTab.setClosable(false);
        VBox homeContent = new VBox(10);
        Label homeLabel = new Label("Welcome to London Air Pollution Data Viewer");
        Label homeInstructions = new Label("Use the tabs above to navigate between different views.");
        homeContent.getChildren().addAll(homeLabel, homeInstructions);
        homeTab.setContent(homeContent);

        mapViewTab = new Tab("Map View");
        mapViewTab.setClosable(false);

        pollutionStatistics = new PollutionStatistics();
        statsTab = new Tab("Pollution Statistics");
        statsTab.setClosable(false);
        statsTab.setContent(pollutionStatistics.getBorderPane());

        gridDataTab = new Tab("Detailed Grid Data");
        gridDataTab.setClosable(false);
        VBox gridContent = new VBox(10);
        Label gridPlaceholder = new Label("Detailed grid data will be displayed here.");
        gridContent.getChildren().addAll(gridPlaceholder);
        gridDataTab.setContent(gridContent);

        tabPane.getTabs().addAll(homeTab, mapViewTab, statsTab, gridDataTab);
        map = new MapImage("resources/London.png");
        mapImage = map.getImage();
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setFitWidth(500);

        anchorPane = new AnchorPane();
        anchorPane.getChildren().add(mapView);
        anchorPane.setMinWidth(500);
        anchorPane.setMinHeight(300);
        mapView.fitWidthProperty().bind(anchorPane.widthProperty());
        mapView.fitHeightProperty().bind(anchorPane.heightProperty());

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(anchorPane);

        HBox bottomBar = new HBox(10);
        bottomBar.setPrefHeight(50);
        bottomBar.setMinHeight(30);
        bottomBar.setMaxHeight(80);

        GridPane rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setPrefWidth(250);
        rightBar.setMinWidth(150);
        rightBar.setMaxWidth(300);

        Label pollutantLabel = new Label("Choose a pollutant:");
        ComboBox<String> pollutantComboBox = new ComboBox<>();
        pollutantComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pollutantSelected = newValue;
                    updateColourMap();
                });
        pollutantComboBox.setPromptText("Pollutant");
        pollutantComboBox.getItems().addAll("pm2.5", "no2", "pm10");

        Label yearLabel = new Label("Choose a year:");
        ComboBox<String> yearComboBox = new ComboBox<>();
        yearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    yearSelected = newValue;
                    updateColourMap();
                });
        yearComboBox.setPromptText("Year");
        yearComboBox.getItems().addAll("2019", "2020", "2021", "2022", "2023");

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

        rightBar.add(pollutantLabel, 0, 0);
        rightBar.add(pollutantComboBox, 0, 1);
        rightBar.add(yearLabel, 0, 2);
        rightBar.add(yearComboBox, 0, 3);
        rightBar.add(dataPointLabel, 0, 4);
        rightBar.add(dataPointValue, 0, 5);
        rightBar.add(gridCodeLabel, 0, 6);
        rightBar.add(gridCodeValue, 0, 7);
        rightBar.add(xLabel, 0, 8);
        rightBar.add(xValue, 0, 9);
        rightBar.add(yLabel, 0, 10);
        rightBar.add(yValue, 0, 11);

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

        trackMouseLocation();

        mapViewTab.setContent(borderPane);
    }

    private void trackMouseLocation() {
//        mapView.setOnMouseClicked(event -> {
//            System.out.println("Mouse Location - X: " + mouseX + ", Y: " + mouseY);
//            double scaleMouseX = (mouseX * (42903 / mapView.getFitWidth()))+510394;
//            double scaleMouseY = 193305-(mouseY * (24801 / mapView.getFitHeight()));
//            System.out.println("Mouse Location Relative to the pic - X: " + (scaleMouseX) + ", Y: " + (scaleMouseY));
//                // Find nearest data point
//            DataPoint nearestDataPoint = findNearestDataPoint(scaleMouseX, scaleMouseY);
//
//            if (nearestDataPoint != null) {
//                System.out.println("Pollution Data at (" + nearestDataPoint.x() + ", " + nearestDataPoint.y() + "):");
//                System.out.println("Grid Code: " + nearestDataPoint.gridCode());
//                System.out.println("Pollutant Value: " + nearestDataPoint.value());
//                    // Convert real-world coordinates to screen coordinates
//                double scaledX = (nearestDataPoint.x() - 510394)/ (42903 / mapView.getFitWidth());
//                double scaledY = ((nearestDataPoint.y() -193305)*-1)/(24801 / mapView.getFitHeight());
//                System.out.println("Scaled X: " + scaledX + ", Scaled Y: " + scaledY);
//                    // Create a circle at the data point location
//                Circle dataPointCircle = new Circle(scaledX, scaledY, 3); // Radius of 10
//                dataPointCircle.setFill(javafx.scene.paint.Color.RED);
//                // Uncomment the following line to add click event for data point info
//                anchorPane.getChildren().add(dataPointCircle);
//                // Show in an alert
//                showDataPointInfo(nearestDataPoint);
//            } else {
//                System.out.println("No pollution data found near this location.");
//            }
//        });

        mapView.setOnMouseClicked(event -> {
            int imageWidth = (int) mapView.getFitWidth();
            int imageHeight = (int) mapView.getFitHeight();
            int x = (int) ((mouseX / (double) imageWidth) * (MAX_X - MIN_X) + MIN_X);
            int y = (int) ((mouseY / (double) imageHeight) * (MAX_Y - MIN_Y) + MIN_Y);

            DataPoint nearestDataPoint = selectedDataSet.findNearestDataPoint(x, y);
            showDataPointInfo(nearestDataPoint);
        });
    }

    private DataPoint findNearestDataPoint(double mouseX, double mouseY) {
        DataPoint nearestDataPoint = null;
        double minDistance = Double.MAX_VALUE;

        for (DataPoint dp : selectedDataSet.getData()) {
            double x = dp.x();
            double y = dp.y();
            double distance = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));

            if (distance < minDistance) {
                minDistance = distance;
                nearestDataPoint = dp;
            }
        }

        return nearestDataPoint;
    }


    private void showDataPointInfo(DataPoint dataPoint) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data Point Information");
        alert.setHeaderText(null);
        alert.setContentText("Grid Code: " + dataPoint.gridCode() + "\nX: " + dataPoint.x() + "\nY: " + dataPoint.y() + "\nValue: " + dataPoint.value());
        alert.showAndWait();
    }

    public void updateColourMap(){
        if (pollutantSelected == null || yearSelected == null) {
            return;
        }
        selectedDataSet = dataAggregator.getDataSet(yearSelected, pollutantSelected);
        for (DataPoint dataPoint : selectedDataSet.getData()) {
            if (dataPoint.value() > 0) {
                map.processDataPoint(dataPoint, selectedDataSet.getMin(), selectedDataSet.getMax());
            }
        }
        Image mapImage = map.getCombined();
        mapView.setImage(mapImage);

    }

    public void updateStats(){
        if (pollutantSelected == null || yearSelected == null) {
            return;
        }
        if (selectedDataSet == null) {
            return;
        }
        int imageWidth = (int) mapView.getFitWidth();
        int imageHeight = (int) mapView.getFitHeight();
        int x = (int) ((mouseX / (double) imageWidth) * (MAX_X - MIN_X) + MIN_X);
        int y = (int) ((mouseY / (double) imageHeight) * (MAX_Y - MIN_Y) + MIN_Y);
        DataPoint nearestDataPoint = selectedDataSet.findNearestDataPoint(x, y);
        dataPointValue.setText(nearestDataPoint.value() + " " + selectedDataSet.getUnits());
        gridCodeValue.setText(String.valueOf(nearestDataPoint.gridCode()));
    }
}