import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.stage.Stage;

/**
 * Creates the window for the main menu.
 *
 * @author Anton Dvidouski, Nicolas Alcala Olea, Yaal Luka Edrey Gatignol,  Rom Steinberg
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

        Scene scene = new Scene(root, 800, 600);
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
        VBox mapContent = new VBox(10);
        Label mapPlaceholder = new Label("Map visualization will be implemented here.");
        mapContent.getChildren().addAll(mapPlaceholder);
        mapViewTab.setContent(mapContent);

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
            mapView.setFitWidth(780);

            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(mapView);

            borderPane.setRight(new VBox(new Label("Right Panel Content")));
            borderPane.setBottom(new HBox(new Label("Bottom Panel Content")));

            mapViewTab.setContent(borderPane);

        } catch (IOException e) {
            Label errorLabel = new Label("Error loading map: " + e.getMessage());
            mapViewTab.setContent(errorLabel);
            e.printStackTrace();
        }
    }

// In your createTabPane method, replace the drawMap method with this:
private void createMapView() {
    mapViewTab = new Tab("Map View");
    mapViewTab.setClosable(false);
    VBox mapContent = new VBox(10);
    
    // Load the map image
    Image mapImage = new Image("C:\\Users\\yaale\\OneDrive - King's College London\\Documents\\king's college london\\semester 2\\programming practice and applications\\assignment 4 - london air pollution\\londonAirPollution\\London.png"); // Replace with your actual map image path
    ImageView mapView = new ImageView(mapImage);
    mapView.setFitWidth(800);
    mapView.setFitHeight(600);
    
    mapContent.getChildren().add(mapView);
    mapViewTab.setContent(mapContent);
}

}