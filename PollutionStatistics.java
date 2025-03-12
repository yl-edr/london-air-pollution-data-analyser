import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PollutionStatistics {

    private static final int MIN_X = 510394;
    private static final int MAX_X = 553297;
    private static final int MIN_Y = 193305;
    private static final int MAX_Y = 168504;

    private MapImage map;
    private Image mapImage;
    private ImageView mapView;

    private AnchorPane mapPane;
    private BorderPane borderPane;
    private VBox chartPane;
    private Chart chart;

    private String pollutantSelected;
    private String yearSelected;

    private Label dataPointValue;
    private Label gridCodeValue;
    private Label xValue;
    private Label yValue;

    private int mouseX;
    private int mouseY;

    private String fromYearSelected;
    private String toYearSelected;

    public PollutionStatistics() {
        borderPane = new BorderPane();

        map = new MapImage("resources/London.png");
        mapImage = map.getImage();
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setFitWidth(400);

        mapPane = new AnchorPane();
        mapPane.getChildren().add(mapView);
        mapPane.setMinWidth(250);
        mapPane.setMinHeight(200);
        //AnchorPane.setLeftAnchor(mapView, 7.0);
        mapView.fitWidthProperty().bind(mapPane.widthProperty());
        mapView.fitHeightProperty().bind(mapPane.heightProperty());
        borderPane.setCenter(mapPane);



        VBox centerVBox = new VBox();
        centerVBox.getChildren().add(mapPane);

        chartPane = new VBox();
        chart = new Chart();
        chartPane.getChildren().add(chart.getChart());

        chart.getChart().prefWidthProperty().bind(mapPane.widthProperty());

        VBox.setVgrow(chart.getChart(), Priority.ALWAYS);

        mapPane.setPrefHeight(400);
        mapPane.setPrefWidth(700);
        chartPane.setPrefHeight(150);

        centerVBox.getChildren().add(chartPane);
        VBox.setVgrow(chartPane, Priority.ALWAYS);

        borderPane.setCenter(centerVBox);

        GridPane rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setPrefWidth(250);
        rightBar.setMinWidth(150);
        rightBar.setMaxWidth(300);

        Label titleLabel = new Label("Statistics");
        titleLabel.getStyleClass().add("titleLabel");

        Label pollutantLabel = new Label("Choose a pollutant:");
        ComboBox<String> pollutantComboBox = new ComboBox<>();
        pollutantComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pollutantSelected = newValue;
                });
        pollutantComboBox.setPromptText("Pollutant");
        pollutantComboBox.getItems().addAll("pm2.5", "no2", "pm10");

        Label fromYearLabel = new Label("From Year:");
        ComboBox<String> fromYearComboBox = new ComboBox<>();
        fromYearComboBox.setPromptText("Select Start Year");
        fromYearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");

        Label toYearLabel = new Label("To Year:");
        ComboBox<String> toYearComboBox = new ComboBox<>();
        toYearComboBox.setPromptText("Select End Year");
        toYearComboBox.getItems().addAll("2018", "2019", "2020", "2021", "2022", "2023");

        fromYearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    fromYearSelected = newVal;
                    validateYearSelection(fromYearSelected, toYearSelected);
        });

        toYearComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    toYearSelected = newVal;
                    validateYearSelection(fromYearSelected, toYearSelected);
        });

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
            // updateStats();
        });

        rightBar.add(titleLabel, 0, 0);
        rightBar.add(pollutantLabel, 0, 1);
        rightBar.add(pollutantComboBox, 0, 2);
        rightBar.add(fromYearLabel, 0, 3);
        rightBar.add(fromYearComboBox, 0, 4);
        rightBar.add(toYearLabel, 0, 5);
        rightBar.add(toYearComboBox, 0, 6);
        rightBar.add(dataPointLabel, 0, 7);
        rightBar.add(dataPointValue, 0, 8);
        rightBar.add(gridCodeLabel, 0, 9);
        rightBar.add(gridCodeValue, 0, 10);
        rightBar.add(xLabel, 0, 11);
        rightBar.add(xValue, 0, 12);
        rightBar.add(yLabel, 0, 13);
        rightBar.add(yValue, 0, 14);



        GridPane.setMargin(dataPointLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(dataPointValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(gridCodeLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(gridCodeValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(xLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(xValue, new Insets(0, 0, 10, 0));
        GridPane.setMargin(yLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(yValue, new Insets(0, 0, 10, 0));

        borderPane.setRight(rightBar);

        //chartPane = new AnchorPane();
        //chart = new Chart();
        //chartPane.getChildren().add(chart.getChart());

        //chart.getChart().prefWidthProperty().bind(mapPane.widthProperty().multiply(0.9));
        //chart.getChart().prefHeightProperty().bind(mapPane.heightProperty().multiply(0.2));

        
        //chartPane.setMinHeight(50);
        //chartPane.setMinWidth(300);

        //BorderPane.setMargin(chartPane, new Insets(5, 20, 5, 2));
        //borderPane.setBottom(chartPane);
    }

    private void validateYearSelection(String fromYear, String toYear) {
        if (fromYear != null && toYear != null) {
            int from = Integer.parseInt(fromYear);
            int to = Integer.parseInt(toYear);

            if (to < from) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Year Selection");
                alert.setHeaderText("Invalid Range");
                alert.setContentText("The 'To Year' cannot be earlier than the 'From Year'!");
                alert.showAndWait();
            }
        }
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }
}
