import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.IOException;

public class MainMenu extends Application
{
    private BorderPane root;
    private TabPane tabPane;

    private Tab homeTab;
    private Tab mapViewTab;
    private Tab statsTab;
    private Tab gridDataTab;

    @Override
    public void start(Stage stage)
    {
        root = new BorderPane();
        createTabPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 950, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("London Air Pollution Data Viewer");
        stage.setScene(scene);
        stage.show();
    }

    private void createTabPane()
    {
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

            VBox rightBar = new VBox(10);
            rightBar.getChildren().add(new Label("Test"));
            rightBar.setPrefWidth(75);
            rightBar.setMinWidth(50);
            rightBar.setMaxWidth(100);

            borderPane.setRight(rightBar);

            HBox aqiBarContainer = new HBox(10);
            aqiBarContainer.setAlignment(Pos.CENTER);
            aqiBarContainer.setPrefHeight(50);
            aqiBarContainer.setMinHeight(30);
            aqiBarContainer.setMaxHeight(80);

            Label lowLabel = new Label("GOOD");
            Label highLabel = new Label("POOR");

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

            mapViewTab.setContent(borderPane);

        } catch (IOException e) {
            Label errorLabel = new Label("Error loading map: " + e.getMessage());
            mapViewTab.setContent(errorLabel);
            e.printStackTrace();
        }
    }
}
