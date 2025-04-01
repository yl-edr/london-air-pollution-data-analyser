import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AppWindow extends Application {

    private BorderPane root;
    private TabPane tabPane;
    private WelcomePanel welcomePanel;

    private Stage welcomeStage;
    private Tab londonTab;
    private Tab statsTab;
    private Tab manchesterTab;
    private Tab predictionTab;

    private DataAggregator dataAggregator;

    @Override
    public void start(Stage stage) {
        createWelcomePanel(stage);

        //Scene scene = new Scene(root, 1150, 650);
        //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());


        //stage.setTitle("London Air Pollution Data Viewer");
        //stage.setScene(scene);
        //stage.show();

        //Alert startAlert = new Alert(Alert.AlertType.INFORMATION);
        //startAlert.setTitle("Loading Data");
        //startAlert.setHeaderText("Please wait...");
        //startAlert.setContentText("Data is being loaded. " + "This popup will close when the data is loaded.");
        //startAlert.initOwner(stage);
        //startAlert.show();

        //Task<Void> dataLoadingTask = new Task<>() {
          //  @Override
            //protected Void call() {
              //  dataAggregator.processDirectory("UKAirPollutionData/NO2/");
                //dataAggregator.processDirectory("UKAirPollutionData/pm10/");
                //dataAggregator.processDirectory("UKAirPollutionData/pm2.5/");
                //return null;
            //}

            //protected void succeeded(){
              //  startAlert.close();
            //}
        //};

        //new Thread(dataLoadingTask).start();
    }

    private void createTabPane() {
        tabPane = new TabPane();

        londonTab = new Tab("London Map");
        londonTab.setClosable(false);

        statsTab = new Tab("Pollution Statistics");
        statsTab.setClosable(false);

        PollutionStatistics statsContent = new PollutionStatistics(dataAggregator);
        statsTab.setContent(statsContent.getBorderPane());

        manchesterTab = new Tab("UK Cities");
        manchesterTab.setClosable(false);

        tabPane.getTabs().addAll(londonTab, statsTab, manchesterTab);
        
        City londonTabAnchor = new LondonTab(dataAggregator);
        londonTab.setContent(londonTabAnchor.getPane());

        City manchesterAnchor = new Manchester(dataAggregator);
        manchesterTab.setContent(manchesterAnchor.getPane());
    }
    public void createWelcomePanel(Stage stage) {
        welcomePanel = new WelcomePanel();
        welcomePanel.createWelcomePanel(new Stage());
        //welcome pane initialisation
    }

    public void createInitialPanel(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("initialPanel");
        dataAggregator = new DataAggregator();
        createTabPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1250, 700);
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
                return null;
            }

            protected void succeeded(){
                startAlert.close();
            }
        };

        new Thread(dataLoadingTask).start();
    }
}