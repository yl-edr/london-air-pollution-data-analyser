import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class CityTest {
    
    private City city;
    private DataAggregator dataAggregator;
    private static boolean jfxIsSetup = false;
    
    // Initialize JavaFX toolkit
    static void initJavaFX() {
        if (!jfxIsSetup) {
            // Initialize the JavaFX platform
            new JFXPanel();
            jfxIsSetup = true;
        }
    }

    @BeforeEach
    public void setUp() {
        initJavaFX();
        
        // Initialize DataAggregator with test data
        dataAggregator = new DataAggregator();
        
        // Create City object
        Platform.runLater(() -> {
            city = new City("London", dataAggregator);
        });
        
        // Wait for JavaFX operations to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCityInitialization() {
        Platform.runLater(() -> {
            assertNotNull(city);
            assertEquals("London", city.name);
            assertNotNull(city.getPane());
            assertNotNull(city.getRightBar());
            assertNotNull(city.getDataAggregator());
        });
    }

    @Test
    public void testGetPane() {
        Platform.runLater(() -> {
            BorderPane pane = city.getPane();
            assertNotNull(pane);
            assertNotNull(pane.getCenter()); // Should have the anchorPane with map
            assertNotNull(pane.getRight()); // Should have the rightBar
            assertNotNull(pane.getBottom()); // Should have the AQI bar
        });
    }

    @Test
    public void testGetRightBar() {
        Platform.runLater(() -> {
            GridPane rightBar = city.getRightBar();
            assertNotNull(rightBar);
            // Test that rightBar contains expected components
            boolean hasPollutantComboBox = false;
            boolean hasYearComboBox = false;
            
            for (javafx.scene.Node node : rightBar.getChildren()) {
                if (node instanceof ComboBox && node.getStyleClass().contains("pollutantComboBox")) {
                    hasPollutantComboBox = true;
                }
                if (node instanceof ComboBox && node.getStyleClass().contains("yearComboBox")) {
                    hasYearComboBox = true;
                }
            }
            
            assertTrue(hasPollutantComboBox, "Right bar should contain pollutant combo box");
            assertTrue(hasYearComboBox, "Right bar should contain year combo box");
        });
    }

    @Test
    public void testGetDataAggregator() {
        Platform.runLater(() -> {
            DataAggregator aggregator = city.getDataAggregator();
            assertNotNull(aggregator);
            assertSame(dataAggregator, aggregator);
        });
    }

    @Test
    public void testCityBoundaries() {
        // Test that city boundaries are properly defined
        assertNotNull(City.getCitiesBoundaries());
        assertTrue(City.getCitiesBoundaries().containsKey("London"));
        assertTrue(City.getCitiesBoundaries().containsKey("Manchester"));
        assertTrue(City.getCitiesBoundaries().containsKey("Birmingham"));
        assertTrue(City.getCitiesBoundaries().containsKey("Leeds"));
        assertTrue(City.getCitiesBoundaries().containsKey("Bristol"));
        
        // Check that the boundary array has correct length
        assertEquals(5, City.getCitiesBoundaries().get("London").length);
    }
}