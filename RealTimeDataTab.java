import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashMap;

public class RealTimeDataTab {

    // colours taken from a chart i found online using a colour picker
    public static final String green = "#5bc63b";
    public static final String yellow = "#f6c142";
    public static final String orange = "#ed6f2d";
    public static final String red = "#ea3223";
    public static final String purple = "#8c43f6";

    private BorderPane searchBorderPane;
    private ApiConnection apiConnection;
    private double lat;
    private double lon;
    private double[] data;
    private String lastLocation;
    private GridPane dataGrid;
    private DataComparison dataComparison;
    private int numberOfCitiesCompared = 0;
    private Label numberOfCitiesComparedLabel;
    private VBox container;

    public RealTimeDataTab() {
        apiConnection = new ApiConnection();
        dataComparison = new DataComparison();
        create();
    }

    public void create() {
        searchBorderPane = new BorderPane();

        TextField searchField = new TextField();
        searchField.setPromptText("Enter location...");
        searchField.setPrefWidth(300);

        searchField.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5px; -fx-border-width: 2px; -fx-font-size: 14px");

        HBox searchBox = new HBox(10);

        searchBox.setPadding(new Insets(20));
        searchBox.getChildren().addAll(searchField);
        searchBox.setAlignment(Pos.CENTER);

        container = new VBox(10);
        container.setAlignment(Pos.TOP_CENTER);
        container.getChildren().add(searchBox);

        dataGrid = new GridPane();
        dataGrid.setHgap(20);
        dataGrid.setVgap(20);
        dataGrid.setPadding(new Insets(20));
        dataGrid.setAlignment(Pos.CENTER);

        numberOfCitiesComparedLabel = new Label("Cities in comparison: " + numberOfCitiesCompared);
        numberOfCitiesComparedLabel.setStyle("-fx-font-size: 14px");
        numberOfCitiesComparedLabel.setVisible(false);

        Button compareAddButton = new Button("Add to comparison");
        compareAddButton.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5px; -fx-border-width: 2px; -fx-font-size: 14px");
        compareAddButton.setVisible(false);
        compareAddButton.setOnMousePressed(event -> addCityToCompare());

        Button compareRemoveButton = new Button("Remove from comparison");
        compareRemoveButton.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5px; -fx-border-width: 2px; -fx-font-size: 14px");
        compareRemoveButton.setVisible(false);
        compareRemoveButton.setOnMousePressed(event -> removeCityFromCompare());

        Button chartButton = new Button("Chart");
        chartButton.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5px; -fx-border-width: 2px; -fx-font-size: 14px");
        chartButton.setVisible(false);
        chartButton.setOnMousePressed(event -> displayChart());

        container.getChildren().addAll(dataGrid, numberOfCitiesComparedLabel, compareAddButton, compareRemoveButton, chartButton);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(50, 50);
        spinner.setVisible(false);

        searchBorderPane.setCenter(container);
        searchBorderPane.setTop(spinner);

        ContextMenu suggestionsPopup = new ContextMenu();

        PauseTransition pause = new PauseTransition(Duration.millis(500));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.stop();
            lat = 0;
            lon = 0;

            if (newValue.trim().isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            pause.setOnFinished(event -> {
                spinner.setVisible(true);

                Task<HashMap<String, double[]>> apiCall = new Task<>() {
                    @Override
                    protected HashMap<String, double[]> call() {
                        return apiConnection.getTopLocationsForSearch(newValue.trim());
                    }
                };

                apiCall.setOnSucceeded(event1 -> {
                    HashMap<String, double[]> results = apiCall.getValue();
                    spinner.setVisible(false);
                    suggestionsPopup.getItems().clear();

                    if (results != null && results.size() > 0) {
                        for (String result : results.keySet()) {
                            MenuItem item = new MenuItem(result);
                            item.setOnAction(actionEvent -> {
                                compareAddButton.setVisible(true);
                                compareRemoveButton.setVisible(true);
                                chartButton.setVisible(true);
                                numberOfCitiesComparedLabel.setVisible(true);

                                searchField.setText(result);
                                searchField.getParent().requestFocus();

                                lastLocation = result;
                                suggestionsPopup.hide();

                                lat = results.get(result)[0];
                                lon = results.get(result)[1];

                                lastLocation = result;
                                displayData(spinner);
                            });
                            suggestionsPopup.getItems().add(item);
                        }
                    } else {
                        MenuItem item = new MenuItem("No results found");
                        suggestionsPopup.getItems().add(item);
                    }

                    if (!suggestionsPopup.isShowing()) {
                        suggestionsPopup.show(searchField, Side.BOTTOM, 0, 0);
                    }
                    spinner.setVisible(false);
                });

                new Thread(apiCall).start();
            });
            pause.playFromStart();
        });

        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                pause.stop();
                suggestionsPopup.hide();
            }
        });

    }
    public void displayData(ProgressIndicator spinner) {
        spinner.setVisible(true);
        Task<Void> apiCall = new Task<>() {
            @Override
            protected Void call() {
                data = apiConnection.getDataForLatLon(lat, lon);
                return null;
            }
        };

        apiCall.setOnSucceeded(event -> {
            spinner.setVisible(false);
            renderData(data, dataGrid);
        });
        new Thread(apiCall).start();
    }

    public BorderPane getPane() {
        return searchBorderPane;
    }

    private void renderData(double[] data, GridPane dataGrid) {
        dataGrid.getChildren().clear();

        if (data == null) {
            dataGrid.add(new Text("Failed to retrieve data."), 0, 0);
            return;
        }

        String[] labels = {"AQI", "CO", "NO2", "O3", "SO2", "PM2.5", "PM10", "NH3"};

        for (int i = 0; i < data.length; i++) {
            double value = data[i];
            String label = labels[i];

            Rectangle rect = new Rectangle(100, 100);
            rect.setArcWidth(25);
            rect.setArcHeight(25);
            rect.setFill(getColor(value, label));

            Text labelText = new Text(label);
            labelText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            labelText.setFill(Color.WHITE);

            Text valueText = new Text(String.format("%.1f", value));
            valueText.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            valueText.setFill(Color.WHITE);

            VBox boxContent = new VBox(5, labelText, valueText);
            boxContent.setAlignment(Pos.CENTER);

            StackPane box = new StackPane(rect, boxContent);

            int row = i / 4;
            int col = i % 4;
            dataGrid.add(box, col, row);
        }
    }

    private void addCityToCompare() {
        if (dataComparison.addCityData(lastLocation, data)) {
            numberOfCitiesCompared++;
            numberOfCitiesComparedLabel.setText("Cities in comparison: " + numberOfCitiesCompared);
        }
    }

    private void removeCityFromCompare() {
        if (dataComparison.removeCityData(lastLocation)) {
            numberOfCitiesCompared--;
            numberOfCitiesComparedLabel.setText("Cities in comparison: " + numberOfCitiesCompared);
        }
    }

    private void displayChart() {
        if (numberOfCitiesCompared < 3) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Not enough data");
            alert.setContentText("Please add at least three cities to compare.");
            alert.show();
            return;
        }
        BorderPane chart = dataComparison.createChart();
        Button back = new Button("Back");
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(back);
        back.setOnMousePressed(event -> hideChart());
        back.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5px; -fx-border-width: 2px; -fx-font-size: 14px");
        chart.setTop(buttonBox);
        searchBorderPane.setCenter(chart);
    }

    private void hideChart() {
        searchBorderPane.setCenter(container);
    }

    private Color getColor(double value, String pollutant) {
        // ranges taken from API source's own documentation:
        // https://openweathermap.org/api/air-pollution
        switch (pollutant) {
            case "AQI":
                return switch ((int) value) {
                    case 1 -> Color.web(green);
                    case 2 -> Color.web(yellow);
                    case 3 -> Color.web(orange);
                    case 4 -> Color.web(red);
                    case 5 -> Color.web(purple);
                    default -> Color.BLACK;
                };
            case "SO2":
                if (value < 20) return Color.web(green);
                else if (value < 80) return Color.web(yellow);
                else if (value < 250) return Color.web(orange);
                else if (value < 350) return Color.web(red);
                else return Color.web(purple);
            case "NO2":
                if (value < 40) return Color.web(green);
                else if (value < 70) return Color.web(yellow);
                else if (value < 150) return Color.web(orange);
                else if (value < 200) return Color.web(red);
                else return Color.web(purple);
            case "PM10":
                if (value < 20) return Color.web(green);
                else if (value < 50) return Color.web(yellow);
                else if (value < 100) return Color.web(orange);
                else if (value < 200) return Color.web(red);
                else return Color.web(purple);
            case "PM2.5":
                if (value < 10) return Color.web(green);
                else if (value < 25) return Color.web(yellow);
                else if (value < 50) return Color.web(orange);
                else if (value < 75) return Color.web(red);
                else return Color.web(purple);
            case "O3":
                if (value < 60) return Color.web(green);
                else if (value < 100) return Color.web(yellow);
                else if (value < 140) return Color.web(orange);
                else if (value < 180) return Color.web(red);
                else return Color.web(purple);
            case "CO":
                if (value < 4400) return Color.web(green);
                else if (value < 9400) return Color.web(yellow);
                else if (value < 12400) return Color.web(orange);
                else if (value < 15400) return Color.web(red);
                else return Color.web(purple);
            case "NH3":
                if (value < 40) return Color.web(green);
                else if (value < 80) return Color.web(yellow);
                else if (value < 120) return Color.web(orange);
                else if (value < 160) return Color.web(red);
                else return Color.web(purple);
            case "NO":
                if (value < 20) return Color.web(green);
                else if (value < 40) return Color.web(yellow);
                else if (value < 60) return Color.web(orange);
                else if (value < 80) return Color.web(red);
                else return Color.web(purple);
            default:
                return Color.BLACK;
        }

    }
}
