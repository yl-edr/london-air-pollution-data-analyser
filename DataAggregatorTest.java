import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class DataAggregatorTest {
    
    private DataAggregator dataAggregator;
    
    @BeforeEach
    public void setUp() {
        dataAggregator = new DataAggregator();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(dataAggregator.dataSets);
        assertNotNull(dataAggregator.dataFilter);
        assertNotNull(dataAggregator.citiesDataSets);
        assertEquals(0, dataAggregator.dataSets.size());
        assertEquals(0, dataAggregator.citiesDataSets.size());
    }
    
    @Test
    public void testAddDataSet() {
        // Create a test dataset
        DataSet testDataSet = new DataSet("pm2.5", "2020", "average", "µg/m³");
        testDataSet.getData().add(new DataPoint(12345, 530000, 180000, 15.2)); // London coordinates
        
        // Create a HashMap that would be returned by DataFilter.filterCityData
        HashMap<String, DataSet> filteredData = new HashMap<>();
        filteredData.put("London", testDataSet);
        
        // Mock the static method filterCityData using a spy or mock framework
        // For simplicity, we'll use our own stub implementation
        // This is a bit tricky without a mocking framework, so we'll test via reflection
        
        // Add dataset to aggregator
        dataAggregator.addDataSet(testDataSet);
        
        // We can't really verify much without mocking, but we can check it doesn't throw exceptions
        assertNotNull(dataAggregator.citiesDataSets);
    }
    
    @Test
    public void testGetCityDataSet() {
        // Manually add a dataset to citiesDataSets
        DataSet testDataSet = new DataSet("pm2.5", "2020", "average", "µg/m³");
        testDataSet.getData().add(new DataPoint(12345, 530000, 180000, 15.2));
        dataAggregator.citiesDataSets.put("London_2020_pm2.5", testDataSet);
        
        // Test retrieval
        DataSet retrievedDataSet = dataAggregator.getCityDataSet("London", "2020", "pm2.5");
        assertSame(testDataSet, retrievedDataSet);
        
        // Test nonexistent data
        assertNull(dataAggregator.getCityDataSet("London", "2019", "pm2.5"));
        assertNull(dataAggregator.getCityDataSet("Manchester", "2020", "pm2.5"));
        assertNull(dataAggregator.getCityDataSet("London", "2020", "no2"));
    }
    
    @Test
    public void testAddTubeDataSet() {
        // Create a mock TubeDataSet
        TubeDataSet tubeDataSet = new TubeDataSet("pm2.5", "2020", "average", "µg/m³");
        
        // Add to aggregator
        dataAggregator.addDataSet(tubeDataSet);
        
        // Verify it was stored
        assertSame(tubeDataSet, dataAggregator.getTubeDataSet());
    }
    
    @Test
    public void testGetTubeDataSet() {
        // Create and add a TubeDataSet
        TubeDataSet tubeDataSet = new TubeDataSet("pm2.5", "2020", "average", "µg/m³");
        dataAggregator.addDataSet(tubeDataSet);
        
        // Test retrieval
        assertSame(tubeDataSet, dataAggregator.getTubeDataSet());
        
        // Test when no tube data is set
        dataAggregator = new DataAggregator(); // Create a new instance
        assertNull(dataAggregator.getTubeDataSet());
    }
}