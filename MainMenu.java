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

public class MainMenu extends Application {
    private BorderPane root;
    private TabPane tabPane;

    private Tab homeTab;
    private Tab mapViewTab;
    private Tab statsTab;
    private Tab gridDataTab;

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
            ImageView mapView = new ImageView(mapImage);
            mapView.setPreserveRatio(true);
            mapView.setSmooth(true);
            mapView.setFitWidth(500);

            AnchorPane anchorPane = new AnchorPane();
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

            VBox rightBar = new VBox(10);
            rightBar.getChildren().add(new Label("Test"));
            rightBar.setPrefWidth(75);
            rightBar.setMinWidth(50);
            rightBar.setMaxWidth(100);

            borderPane.setBottom(bottomBar);
            borderPane.setRight(rightBar);

            // Load data and overlay data points
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile("UKAirPollutionData/NO2/mapno22023.csv");
            if (dataSet != null) {
                // Calculate min and max values for scaling
                int minX = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxY = Integer.MIN_VALUE;

                for (DataPoint dataPoint : dataSet.getData()) {
                    if (dataPoint.x() < minX) minX = dataPoint.x();
                    if (dataPoint.x() > maxX) maxX = dataPoint.x();
                    if (dataPoint.x() < minX) minX = dataPoint.x();
                    if (dataPoint.x() > maxX) maxX = dataPoint.x();
                    if (dataPoint.y() < minY) minY = dataPoint.y();
                    if (dataPoint.y() > maxY) maxY = dataPoint.y();
                }

                // Calculate scaling factors
                double scaleX = mapView.getFitWidth() / (maxX - minX);
                double scaleY = mapView.getFitHeight() / (maxY - minY);

                // Overlay data points on the map
                for (DataPoint dataPoint : dataSet.getData()) {
                    double x = mapView.getX() + (dataPoint.x() - minX) * scaleX;
                    double y = mapView.getY() + (maxY - dataPoint.y()) * scaleY;

                    Circle dataPointCircle = new Circle(x, y, 5);
                    dataPointCircle.setFill(javafx.scene.paint.Color.RED);
                    dataPointCircle.setOnMouseClicked(event -> showDataPointInfo(dataPoint));
                    anchorPane.getChildren().add(dataPointCircle);
                }
            }

            mapViewTab.setContent(borderPane);

        } catch (IOException e) {
            Label errorLabel = new Label("Error loading map: " + e.getMessage());
            mapViewTab.setContent(errorLabel);
            e.printStackTrace();
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

