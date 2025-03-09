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
        mapPane.setMinWidth(400);
        mapPane.setMinHeight(240);
        mapView.fitWidthProperty().bind(mapPane.widthProperty());
        mapView.fitHeightProperty().bind(mapPane.heightProperty());
        borderPane.setCenter(mapPane);

    }

    public BorderPane getBorderPane() {
        return borderPane;
    }
}
