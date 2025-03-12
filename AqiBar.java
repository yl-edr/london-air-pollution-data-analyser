import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.geometry.Insets;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * A class that represents an Air Quality Index (AQI) indicator bar with a movable arrow
 * that shows the current pollution level.
 */
public class AqiBar {
    private HBox aqiBarContainer;
    private Region aqiBar;
    private Polygon arrow;
    private StackPane aqiStack;
    private Label valueLabel;
    private Label categoryLabel;

    private DoubleProperty position = new SimpleDoubleProperty(0.0);

    /**
     * Creates a new AQI Indicator with an arrow and labels
     */
    public AqiBar() {
        aqiBarContainer = new HBox(10);
        aqiBarContainer.setAlignment(Pos.CENTER);
        aqiBarContainer.setPrefHeight(80);
        aqiBarContainer.setMinHeight(50);
        aqiBarContainer.setMaxHeight(100);
        aqiBarContainer.setPadding(new Insets(10));

        Label lowLabel = new Label("GOOD");
        Label highLabel = new Label("POOR");
        lowLabel.setMinWidth(Region.USE_PREF_SIZE);
        highLabel.setMinWidth(Region.USE_PREF_SIZE);

        // Create the color gradient bar
        aqiBar = new Region();
        aqiBar.setPrefHeight(30);
        aqiBar.setMaxWidth(Double.MAX_VALUE);
        aqiBar.getStyleClass().add("aqiBar");

        // Create the arrow indicator
        arrow = new Polygon();
        arrow.getPoints().addAll(
                0.0, 0.0,
                10.0, 15.0,
                -10.0, 15.0
        );
        arrow.setFill(Color.BLACK);

        // Create a value label to show below the arrow
        valueLabel = new Label("");
        valueLabel.setStyle("-fx-font-weight: bold;");

        // Add a category label
        categoryLabel = new Label("");
        categoryLabel.setStyle("-fx-font-weight: bold;");

        // Create a stack pane to hold both the bar and the arrow
        aqiStack = new StackPane();
        aqiStack.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(aqiStack, Priority.ALWAYS);
        aqiBar.prefWidthProperty().bind(aqiStack.widthProperty());

        // Add the arrow and bar to the stack pane
        aqiStack.getChildren().addAll(aqiBar, arrow, valueLabel);

        // Position the arrow above the bar
        StackPane.setMargin(arrow, new Insets(0, 0, 0, 0));
        StackPane.setMargin(valueLabel, new Insets(20, 0, 0, 0));

        // Add everything to the container
        HBox labelBox = new HBox(10);
        labelBox.setAlignment(Pos.CENTER);
        labelBox.getChildren().add(categoryLabel);

        aqiBarContainer.getChildren().addAll(lowLabel, aqiStack, highLabel);

        // Bind the arrow position to the position property
        position.addListener((obs, oldVal, newVal) -> {
            double barWidth = aqiStack.getWidth();
            double newPos = barWidth * newVal.doubleValue();
            arrow.setTranslateX(newPos - (barWidth / 2));

            // Update the text label with the AQI category
            updateAQILabels(newVal.doubleValue());
        });
    }

    /**
     * Updates the position of the arrow based on a pollution value
     * @param value Pollution value normalized between 0.0 (low) and 1.0 (high)
     */
    public void updatePosition(double value) {
        double clampedValue = Math.min(1.0, Math.max(0.0, value));
        position.set(clampedValue);
    }

    /**
     * Updates the AQI value label and category based on position
     * @param position Position value between 0.0 and 1.0
     */
    private void updateAQILabels(double position) {
        String category;
        if (position < 0.25) {
            category = "GOOD";
            categoryLabel.setTextFill(Color.GREEN);
        } else if (position < 0.5) {
            category = "MODERATE";
            categoryLabel.setTextFill(Color.YELLOW);
        } else if (position < 0.75) {
            category = "UNHEALTHY";
            categoryLabel.setTextFill(Color.ORANGE);
        } else {
            category = "HAZARDOUS";
            categoryLabel.setTextFill(Color.RED);
        }

        int aqiValue = (int)(position * 500);
        valueLabel.setText(String.valueOf(aqiValue));
        categoryLabel.setText(category);
    }

    /**
     * Get the container that holds the AQI bar
     * @return The HBox container
     */
    public HBox getContainer() {
        return aqiBarContainer;
    }

    /**
     * Calculate the AQI normalized position from a data point's value and min/max range
     * @param value The current data value
     * @param min The minimum value in the dataset
     * @param max The maximum value in the dataset
     * @return A normalized position between 0.0 and 1.0
     */
    public static double calculateNormalizedPosition(double value, double min, double max) {
        if (max == min) return 0.5; // Avoid division by zero
        return (value - min) / (max - min);
    }
}
