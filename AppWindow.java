import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AppWindow extends Application {

    private BorderPane root;
    private TabPane tabPane;

    private Tab homeTab;
    private Tab londonTab;
    private Tab statsTab;
    private Tab gridDataTab;
    private static Tab UKTab;
    private Tab tubeTab;

    private DataAggregator dataAggregator;
    private DataAggregator tubeDataAggregator;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        dataAggregator = new DataAggregator();
        tubeDataAggregator = new DataAggregator();
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
                dataAggregator.processDirectory("UKAirPollutionData/NO2/");
                dataAggregator.processDirectory("UKAirPollutionData/pm10/");
                dataAggregator.processDirectory("UKAirPollutionData/pm2.5/");
                tubeDataAggregator.processDirectory("UKAirPollutionData/Tube/");
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

        londonTab = new Tab("London Map");
        londonTab.setClosable(false);

        statsTab = new Tab("Pollution Statistics");
        statsTab.setClosable(false);

        UKTab = new Tab("UK Cities");
        UKTab.setClosable(false);


        gridDataTab = new Tab("Detailed Grid Data");
        gridDataTab.setClosable(false);
        VBox gridContent = new VBox(10);
        Label gridPlaceholder = new Label("Detailed grid data will be displayed here.");
        gridContent.getChildren().addAll(gridPlaceholder);
        gridDataTab.setContent(gridContent);


        tubeTab = new Tab("Tube Journey");
        tubeTab.setClosable(false);

        tabPane.getTabs().addAll(homeTab, londonTab, statsTab, gridDataTab, UKTab, tubeTab);
        
        City londonTabAnchor = new City("London", dataAggregator);
        londonTab.setContent(londonTabAnchor.getPane());

        PollutionStatistics statsContent = new PollutionStatistics(dataAggregator);
        statsTab.setContent(statsContent.getBorderPane());

        City UKcities = new City("Manchester", dataAggregator);
        UKTab.setContent(UKcities.getPane());

        Tube tube = new Tube(tubeDataAggregator, message -> 
        new Alert(Alert.AlertType.ERROR, message).showAndWait());
        tubeTab.setContent(tube.getPane());

    }
    public static void setUKCities(City ukCities) {
        UKTab.setContent(ukCities.getPane());
        
    }
    
}