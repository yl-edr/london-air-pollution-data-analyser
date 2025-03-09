import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class PollutionStatistics {

    private MapImage map;
    private Image mapImage;
    private ImageView mapView;
    private AnchorPane mapPane;
    private BorderPane borderPane;
    private Chart chart;

    public PollutionStatistics() {
        borderPane = new BorderPane();

        // Initialize the map
        map = new MapImage("resources/London.png");
        mapImage = map.getImage();
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(true);
        mapView.setFitWidth(400);

        // Create the anchor pane for the map
        mapPane = new AnchorPane();
        mapPane.getChildren().add(mapView);
        mapPane.setMinWidth(350);
        mapPane.setMinHeight(350);
        mapView.fitWidthProperty().bind(mapPane.widthProperty());
        mapView.fitHeightProperty().bind(mapPane.heightProperty());
        borderPane.setCenter(mapPane);

        // Initialize the line chart
        Chart chart = new Chart();
        borderPane.setBottom(chart.getChart()); // Set the line chart at the bottom

        // Bind the size of the line chart to the anchor pane
        chart.getChart().prefWidthProperty().bind(mapPane.widthProperty().multiply(0.5));
        chart.getChart().prefHeightProperty().bind(mapPane.heightProperty().multiply(0.5));

        BorderPane.setMargin(chart.getChart(), new Insets(15));
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }
}
