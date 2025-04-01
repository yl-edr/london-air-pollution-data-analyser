import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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
    private TextArea journeyDetailsText;
    private Button viewDetailsButton;
    

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
        startStnText.setMaxWidth(150);
        startStnText.setPromptText("Type Station Name");
        Label endStnLabel = new Label("Enter End Station:");
        TextField endStnText = new TextField();
        endStnText.setMaxWidth(150);
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
        
        viewDetailsButton = new Button("View Journey Details");
        viewDetailsButton.setVisible(false); // Hidden initially
        viewDetailsButton.setOnAction(e -> showJourneyDetailsPopup());


        journeyInfoContainer = new VBox(10);
        journeyInfoContainer.setPadding(new Insets(10));
        journeyInfoContainer.setVisible(false); 

        journeyLabel = new Label ();
        journeyLabel.setVisible(false);
        journeyLabel.setWrapText(true);
        journeyLabel.setPadding(new Insets(10));

        journeyDetailsText = new TextArea();
        journeyDetailsText.setEditable(false);
        journeyDetailsText.setWrapText(true);
        journeyDetailsText.setPrefHeight(300);
        journeyDetailsText.setPrefWidth(300);

        journeyInfoContainer.getChildren().addAll(journeyLabel);

        rightBar.add(startStnLabel, 0, 0);
        rightBar.add(startStnText, 0, 1);
        rightBar.add(endStnLabel, 0, 2);
        rightBar.add(endStnText, 0, 3);
        rightBar.add(button, 0, 4);
        rightBar.add(journeyLabel, 0, 6);
        rightBar.add(viewDetailsButton, 0, 7);
        
        GridPane.setMargin(viewDetailsButton, new Insets(10, 0, 0, 0));
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
        convertToDataPoints();
        viewDetailsButton.setVisible(true);
    }

    public void printData() {
        System.out.println(dataAggregator.getTubeDataSet().getMetric());
    }
    public TubeDataSet getTubeDataSet() {
        return dataAggregator.getTubeDataSet();
    }

    private void showJourneyDetailsPopup() {
        Stage popupStage = new Stage();
        popupStage.setTitle("Journey Details");

        VBox popupLayout = new VBox(10);
        popupLayout.setPadding(new Insets(10));

        TextArea journeyTextArea = new TextArea(journeyDetailsText.getText());
        journeyTextArea.setEditable(false);
        journeyTextArea.setWrapText(true);
        //journeyTextArea.setPrefHeight()
        journeyTextArea.setPrefSize(400, 300);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());

        popupLayout.getChildren().addAll(journeyTextArea, closeButton);

        Scene popupScene = new Scene(popupLayout, 420, 350);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private void convertToDataPoints(){
        List<String> journey = tubeSystem.calculateJourney(startStn, endStn);
        double totalBG = 0;
        double totalOG = 0;
        String journeyDetails = "Your journey goes through*: \n";
        journeyDetails +="------------------------------\n";
        for (String station : journey) {
            TubeDataPoint tdp = getTubeDataSet().findStationData(station);
            totalBG += tdp.tubeData();
            totalOG += tdp.streetData();
            journeyDetails += station + " (" + tdp.tubeData() +"ug m^3 PM2.5)" + "\n";
        }
        journeyDetails +="------------------------------\n";
        journeyDetails += "Average pollution below ground: " + ((int)(totalBG * 100)/100)/journey.size() + "\n";
        journeyDetails += "Average pollution on street level: " + (Math.round(totalOG * 100)/100)/journey.size();
        journeyDetails += "\n\n**Note: The data is an estimate taken from a research made in 2020 for PM2.5 pollution leveles in selected stations around London.";
        journeyDetails += "\nThe research can be found in this link: https://content.tfl.gov.uk/dust-monitoring-lu-stations.pdf";
        journeyLabel.setText("By taking the Tube you will be exposed to " +Math.round(totalBG/(Math.round(totalOG * 100)/100)) + " times more PM2.5 compared to street level." + "\n");

        journeyDetailsText.setText(journeyDetails);
        journeyLabel.setVisible(true);
        journeyInfoContainer.setVisible(true);
    }

    public BorderPane getPane() {
        return borderPane;
    }
    
}
