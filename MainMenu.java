import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.stage.Stage;
import javafx.geometry.Insets;

/**
 * Creates the window for the main menu.
 *
 * @author Anton Davidouski, Nicolas Alcala Olea, Yaal Luka Edrey Gatignol,  Rom Steinberg
 * @version v1.0
 */
public class MainMenu extends Application
{
    private BorderPane root;
    private TabPane tabPane;

    private Tab homeTab;
    private Tab mapViewTab;
    private Tab statsTab;
    private Tab gridDataTab;

    private AnchorPane anchorPane;
    private DataSet londonDataSet;
    private ImageView mapView;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        createTabPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 950, 600);

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("London Air Pollution Data Viewer");
        stage.setScene(scene);

        stage.show();
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

        statsTab = new Tab("Pollution Statistics");
        statsTab.setClosable(false);
        VBox statsContent = new VBox(10);
        Label statsPlaceholder = new Label("Pollution statistics will be displayed here.");
        statsContent.getChildren().addAll(statsPlaceholder);
        statsTab.setContent(statsContent);

        gridDataTab = new Tab("Detailed Grid Data");
        gridDataTab.setClosable(false);
        VBox gridContent = new VBox(10);
        Label gridPlaceholder = new Label("Detailed grid data will be displayed here.");
        gridContent.getChildren().addAll(gridPlaceholder);
        gridDataTab.setContent(gridContent);

        tabPane.getTabs().addAll(homeTab, mapViewTab, statsTab, gridDataTab);

        try {
            FileInputStream input = new FileInputStream("resources/London.png");
            Image mapImage = new Image(input);
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
            bottomBar.getChildren().add(new Label("Test"));
            bottomBar.setPrefHeight(50);
            bottomBar.setMinHeight(30);
            bottomBar.setMaxHeight(80);

            GridPane rightBar = new GridPane();
            rightBar.setPadding(new Insets(10));
            rightBar.setPrefWidth(150);
            rightBar.setMinWidth(50);
            rightBar.setMaxWidth(200);

            Label pollutantLabel = new Label("Choose a pollutant:");
            ComboBox<String> pollutantComboBox = new ComboBox<>();
            pollutantComboBox.setPromptText("Pollutant");
            pollutantComboBox.getItems().addAll("Pm2.5", "No2", "Pm10");

            Label yearLabel = new Label("Choose a year:");
            ComboBox<String> yearComboBox = new ComboBox<>();
            yearComboBox.setPromptText("Year");
            yearComboBox.getItems().addAll("2019", "2020", "2021", "2022", "2023");

            Label dataPointLabel = new Label("Value: ");
            Label dataPointValue = new Label("select a data point");

            Label gridCodeLabel = new Label("Grid Code: ");
            Label gridCodeValue = new Label("select a data point");

            Label xLabel = new Label("X: ");
            Label xValue = new Label("select a data point");

            Label yLabel = new Label("Y: ");
            Label yValue = new Label("select a data point");

            rightBar.add(pollutantLabel, 0, 0);
            rightBar.add(pollutantComboBox, 0, 1);
            rightBar.add(yearLabel, 0, 3);
            rightBar.add(yearComboBox, 0, 4);
            rightBar.add(dataPointLabel, 0, 6);
            rightBar.add(dataPointValue, 0, 7);
            rightBar.add(gridCodeLabel, 0, 8);
            rightBar.add(gridCodeValue, 0, 9);
            rightBar.add(xLabel, 0, 10);
            rightBar.add(xValue, 0, 11);
            rightBar.add(yLabel, 0, 12);
            rightBar.add(yValue, 0, 13);

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

            borderPane.setBottom(bottomBar);
            borderPane.setRight(rightBar);

            // Load data and overlay data points
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile("UKAirPollutionData/NO2/mapno22023.csv");
            londonDataSet = DataFilter.filterLondonData(dataSet);
            if (dataSet != null) {

                // Overlay data points on the map
                /*for (DataPoint dataPoint : londonDataSet.getData()) {
                    int testX = dataPoint.x() - 510394;
                    int testY = dataPoint.y() - 168504;
                    System.out.println(testX + " " + testY);
                    Circle dataPointCircle = new Circle(50, 50, 10);
                    dataPointCircle.setFill(javafx.scene.paint.Color.RED);
                    dataPointCircle.setOnMouseClicked(event -> showDataPointInfo(dataPoint));
                    anchorPane.getChildren().add(dataPointCircle);
                }*/
                updateDataPointCircles();
            }

            anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> updateDataPointCircles());
            anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> updateDataPointCircles());

            mapViewTab.setContent(borderPane);

        } catch (IOException e) {
            Label errorLabel = new Label("Error loading map: " + e.getMessage());
            mapViewTab.setContent(errorLabel);
            e.printStackTrace();
        }
    }

    private void updateDataPointCircles() {
        // Clear existing circles
        anchorPane.getChildren().removeIf(node -> node instanceof Circle);

        // Calculate the scale factors based on the original image size
        double scaleX = mapView.getFitWidth() / mapView.getImage().getWidth();
        double scaleY = mapView.getFitHeight() / mapView.getImage().getHeight();

        System.out.println("this is getImage " + mapView.getImage().getWidth() + " " + mapView.getImage().getHeight());
        System.out.println("this is regular " + mapView.getFitWidth() + " " + mapView.getFitHeight());

        //System.out.println(scaleX + " " + scaleY);

        // Create and position circles based on the data points
        for (DataPoint dataPoint : londonDataSet.getData()) {            
            double x = (dataPoint.x()- 510394)* scaleX ; // Scale the x coordinate
            double y = (dataPoint.y() - 168504) * scaleY; // Scale the y coordinate
            //System.out.println(x + " " + y);
            System.out.println(anchorPane.getWidth() + " " + anchorPane.getHeight());
            //System.out.println("this is getImage" + mapView.getImage().getWidth() + " " + mapView.getImage().getHeight());
            //System.out.println("this is regular" + mapView.getFitWidth() + " " + mapView.getFitHeight());
            Circle dataPointCircle = new Circle(x, y, 10); // Radius of 10
            dataPointCircle.setFill(javafx.scene.paint.Color.RED);
            // Uncomment the following line to add click event for data point info
            // dataPointCircle.setOnMouseClicked(event -> showDataPointInfo(dataPoint));
            anchorPane.getChildren().add(dataPointCircle);
        }
    }

    private void showDataPointInfo(DataPoint dataPoint) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data Point Information");
        alert.setHeaderText(null);
        alert.setContentText("Grid Code: " + dataPoint.gridCode() + "\nX: " + dataPoint.x() + "\nY: " + dataPoint.y() + "\nValue: " + dataPoint.value());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

