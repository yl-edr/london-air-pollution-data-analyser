import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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

            HBox bottomBar = new HBox(10);
            bottomBar.getChildren().add(new Label("Test"));
            bottomBar.setPrefHeight(50);
            bottomBar.setMinHeight(30);
            bottomBar.setMaxHeight(80);

// Create a GridPane for the right sidebar
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

            rightBar.add(pollutantLabel, 0, 0);
            rightBar.add(pollutantComboBox, 0, 1);
            rightBar.add(yearLabel, 0, 3);
            rightBar.add(yearComboBox, 0, 4);

            GridPane.setMargin(yearLabel, new Insets(10, 0, 0, 0));

            borderPane.setBottom(bottomBar);
            borderPane.setRight(rightBar);

            mapViewTab.setContent(borderPane);

        } catch (IOException e) {
            Label errorLabel = new Label("Error loading map: " + e.getMessage());
            mapViewTab.setContent(errorLabel);
            e.printStackTrace();
        }
    }

}