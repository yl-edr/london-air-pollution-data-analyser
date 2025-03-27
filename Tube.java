import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

public class Tube {
    private DataAggregator dataAggregator;
    private MapImage map;
    private Image mapImage;
    private ImageView mapView;
    private AnchorPane anchorPane;
    private double mapImageAspectRatio;
    private BorderPane borderPane;
    private String endStn;
    private String startStn;
    private TubeSystem tubeSystem;
    private ErrorHandler errorHandler;
    private VBox journeyInfoContainer;
    private Label journeyLabel;
    private Label journeyDetailsLabel;
    private TextArea journeyDetailsText;
    

    public Tube(DataAggregator dataAggregator, ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        this.dataAggregator = dataAggregator;
        tubeSystem = new TubeSystem();
        create();
    }

    public void create() { 
        map = new MapImage("resources/TubeMap.png");
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

        GridPane rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setPrefWidth(250);
        rightBar.setMinWidth(150);
        rightBar.setMaxWidth(300);

        Label startStnLabel = new Label("Enter Starting Station:");
        TextField startStnText = new TextField();
        startStnText.setPromptText("Type Station Name");
        Label endStnLabel = new Label("Enter End Station:");
        TextField endStnText = new TextField();
        endStnText.setPromptText("Type Station Name");

        Button button = new Button("Check Journey");

        borderPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                submitAction(startStnText, endStnText);
            }
        });
        button.setOnAction(e -> {
            submitAction(startStnText, endStnText);
        });
        

        journeyInfoContainer = new VBox(10);
        journeyInfoContainer.setPadding(new Insets(10));
        journeyInfoContainer.setVisible(false); 

        journeyLabel = new Label ();
        journeyLabel.setVisible(false);

        journeyDetailsLabel = new Label("Journey Details:");
        journeyDetailsText = new TextArea();
        journeyDetailsText.setEditable(false);
        journeyDetailsText.setWrapText(true);
        journeyDetailsText.setPrefHeight(300);
        journeyDetailsText.setPrefWidth(300);

        journeyInfoContainer.getChildren().addAll(journeyLabel, journeyDetailsLabel, journeyDetailsText);

        rightBar.add(startStnLabel, 0, 0);
        rightBar.add(startStnText, 0, 1);
        rightBar.add(endStnLabel, 0, 2);
        rightBar.add(endStnText, 0, 3);
        rightBar.add(button, 0, 4);
        rightBar.add(journeyInfoContainer, 0, 6);

        GridPane.setMargin(startStnLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(startStnText, new Insets(0, 0, 10, 0));
        GridPane.setMargin(endStnLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(endStnText, new Insets(0, 0, 10, 0));
        GridPane.setMargin(button, new Insets(10, 0, 0, 0));
        GridPane.setMargin(journeyInfoContainer, new Insets(10, 0, 0, 0));

        borderPane.setRight(rightBar);

    }

    public void submitAction(TextField startStnText, TextField endStnText) {
        startStn = startStnText.getText().toLowerCase(); 
        endStn = endStnText.getText().toLowerCase();
        if (startStn.isEmpty() || endStn.isEmpty() || getTubeDataSet().findStationData(startStn.trim()) == null || getTubeDataSet().findStationData(endStn.trim()) == null) {
            errorHandler.showError("Please enter a valid station name.");
            return;
        }
        startStn = startStn.replaceAll("and", "&").replace("'", "");
        endStn = endStn.replaceAll("and", "&").replace("'", "");
        System.out.println("User typed: " + startStn+" to "+endStn);
        System.out.println("You started at "+getTubeDataSet().findStationData(startStn.trim()));
        System.out.println("You finished at "+getTubeDataSet().findStationData(endStn.trim()));
        System.out.println(tubeSystem.calculateJourney(startStn, endStn));
        System.out.println("--------------------");
        convertToDataPoints();
    }

    public void printData() {
        System.out.println(dataAggregator.getTubeDataSet().getMetric());
    }
    public TubeDataSet getTubeDataSet() {
        return dataAggregator.getTubeDataSet();
    }

    private void convertToDataPoints(){
        List<String> journey = tubeSystem.calculateJourney(startStn, endStn);
        double totalBG = 0;
        double totalOG = 0;
        String journeyDetails = "";
        for (String station : journey) {
            TubeDataPoint tdp = getTubeDataSet().findStationData(station);
            totalBG += tdp.tubeData();
            totalOG += tdp.streetData();
            journeyDetails += station + " - " + tdp.tubeData() + "\n";
            System.out.println(station + " " + tdp.tubeData());
        }
        journeyDetails += "Average pollution below ground: " + ((int)(totalBG * 100)/100)/journey.size() + "\n";
        journeyDetails += "Average pollution on street level: " + (Math.round(totalOG * 100)/100)/journey.size();
        journeyLabel.setText("By taking the Tube you will be exposed to " +Math.round(totalBG/(Math.round(totalOG * 100)/100)) + " times more PM2.5 compared to street level." + "\n");

        journeyDetailsText.setText(journeyDetails);
        System.out.println("Total: " + totalBG);
        System.out.println("Average pollution below ground" + totalBG/journey.size());
        journeyLabel.setVisible(true);
        //journeyInfoContainer.setVisible(true);
    }

    public BorderPane getPane() {
        return borderPane;
    }
    
}
