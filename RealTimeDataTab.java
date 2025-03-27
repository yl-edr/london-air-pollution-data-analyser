import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.util.HashMap;

public class RealTimeDataTab {


    // min and max Y are swapped because the top left of the image is actually the max Y
    private static final int MIN_X = 510394;
    private static final int MAX_X = 553297;
    private static final int MIN_Y = 193305;
    private static final int MAX_Y = 168504;

    private DataSet selectedDataSet;
    private MapImage map;
    private Image mapImage;
    private ImageView mapView;
    private AnchorPane anchorPane;
    private double mapImageAspectRatio;
    private Label dataPointValue;
    private Label gridCodeValue;
    private Label xValue;
    private Label yValue;
    private String pollutantSelected;
    private BorderPane borderPane;
    private Button refreshButton;
    private ApiConnection apiConnection;
    private HashMap<String, DataSet> allData;

    private int mouseX;
    private int mouseY;

    public RealTimeDataTab() {

        apiConnection = new ApiConnection();
        apiConnection.parseConversionCSV();
        allData = new HashMap<>();
        borderPane = new BorderPane();
        borderPane.setCenter(new Label("LOADING DATA FROM API..."));
        anchorPane = new AnchorPane();

        Task<Void> dataLoadingTask = new Task<>() {
            @Override
            protected Void call() {
//                allData = apiConnection.updateDataSet();
                return null;
            }

            protected void succeeded(){
                create();
                trackMouseLocation();
            }
        };

        new Thread(dataLoadingTask).start();

    }

    public void create() {

        map = new MapImage("resources/fullUK.png");
        mapImage = map.getImage();
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        // Remove fixed sizing so that the image displays at its natural resolution.
         mapView.setFitWidth(1500); // Removed to allow scrolling at full resolution.
        mapImageAspectRatio = mapImage.getWidth() / mapImage.getHeight();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mapView);
        scrollPane.setPannable(true);
        scrollPane.setPrefViewportWidth(500);
        scrollPane.setPrefViewportHeight(300);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);

        borderPane.setCenter(scrollPane);

        GridPane rightBar = new GridPane();
        rightBar.setPadding(new Insets(10));
        rightBar.setPrefWidth(250);
        rightBar.setMinWidth(150);
        rightBar.setMaxWidth(300);

        Label pollutantLabel = new Label("Choose a pollutant:");
        ComboBox<String> pollutantComboBox = new ComboBox<>();
        pollutantComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    pollutantSelected = newValue;
                    updateColourMap();
                });
        pollutantComboBox.setPromptText("Pollutant");
        pollutantComboBox.getItems().addAll("AQI", "CO", "NO", "NO2", "O3", "SO2", "PM2.5", "PM10", "NH3");

        refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> {
            boolean updatePossible = apiConnection.canMakeFullDataSetRequest();
            if (updatePossible) {
                allData = apiConnection.updateDataSet();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("API Request Limit Reached");
                alert.setHeaderText(null);
                alert.setContentText("API request limit reached. Please try again in 30 seconds.");
                alert.showAndWait();
            }
        });

        rightBar.add(pollutantLabel, 0, 0);
        rightBar.add(pollutantComboBox, 0, 1);

        GridPane.setMargin(pollutantLabel, new Insets(10, 0, 0, 0));
        GridPane.setMargin(pollutantComboBox, new Insets(0, 0, 10, 0));

        borderPane.setRight(rightBar);

        HBox aqiBarContainer = new HBox(10);
        aqiBarContainer.setAlignment(Pos.CENTER);
        aqiBarContainer.setPrefHeight(50);
        aqiBarContainer.setMinHeight(30);
        aqiBarContainer.setMaxHeight(80);
        aqiBarContainer.setPadding(new Insets(10));
        BorderPane.setMargin(aqiBarContainer, new Insets(10));

        Label lowLabel = new Label("GOOD");
        Label highLabel = new Label("POOR");
        lowLabel.setMinWidth(Region.USE_PREF_SIZE);
        highLabel.setMinWidth(Region.USE_PREF_SIZE);

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
    }


    public void updateStats(){
        if (selectedDataSet == null) {
            return;
        }
        int[] imageDimensions = convertMapViewDimensionsToImageDimensions((int) mapView.getFitWidth(), (int) mapView.getFitHeight());
        int imageWidth = imageDimensions[0];
        int imageHeight = imageDimensions[1];
        int x = (int) ((mouseX / (double) imageWidth) * (MAX_X - MIN_X) + MIN_X);
        int y = (int) (MIN_Y - ((mouseY / (double) imageHeight) * (MIN_Y - MAX_Y)));
        DataPoint nearestDataPoint = selectedDataSet.findNearestDataPoint(x, y);
        dataPointValue.setText(nearestDataPoint.value() + " " + selectedDataSet.getUnits());
        gridCodeValue.setText(String.valueOf(nearestDataPoint.gridCode()));
    }

    public BorderPane getPane() {
        return borderPane;
    }

    public void updateColourMap(){
        if (pollutantSelected == null) {
            return;
        }
        selectedDataSet = allData.get(pollutantSelected);
        for (DataPoint dataPoint : selectedDataSet.getData()) {
            if (dataPoint.value() > 0) {
                map.processDataPoint(dataPoint, selectedDataSet.getMin(), selectedDataSet.getMax());
            }
        }
        map.applyBlur(60);
        Image mapImage = map.getCombined();
        mapView.setImage(mapImage);
    }

    public int[] convertMapViewDimensionsToImageDimensions(int x, int y) {
        double providedAspectRatio = (double) x / y;
        int[] dimensions = new int[2];
        if (providedAspectRatio > mapImageAspectRatio) {
            dimensions[0] = (int) (y * mapImageAspectRatio);
            dimensions[1] = y;
        } else {
            dimensions[0] = x;
            dimensions[1] = (int) (x / mapImageAspectRatio);
        }
        return dimensions;
    }

    private void trackMouseLocation() {
        mapView.setOnMouseClicked(event -> {
            if (selectedDataSet != null) {
                int[] imageDimensions = convertMapViewDimensionsToImageDimensions((int) mapView.getFitWidth(), (int) mapView.getFitHeight());
                int imageWidth = imageDimensions[0];
                int imageHeight = imageDimensions[1];
                int x = (int) ((mouseX / (double) imageWidth) * (MAX_X - MIN_X) + MIN_X);
                int y = (int) (MIN_Y - ((mouseY / (double) imageHeight) * (MIN_Y - MAX_Y)));
                DataPoint nearestDataPoint = selectedDataSet.findNearestDataPoint(x, y);
                showDataPointInfo(nearestDataPoint);
            }
        });
    }

    private void showDataPointInfo(DataPoint dataPoint) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data Point Information");
        alert.setHeaderText(null);
        alert.setContentText("Grid Code: " + dataPoint.gridCode() + "\nX: " + dataPoint.x() + "\nY: " + dataPoint.y() + "\nValue: " + dataPoint.value());
        alert.showAndWait();
    }

}
