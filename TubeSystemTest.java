import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;

public class TubeSystemTest {
    
    private TubeSystem tubeSystem;
    
    @Before
    public void setUp() {
        tubeSystem = new TubeSystem();
    }
    
    @Test
    public void testCalculateJourney_SameLineDirectJourney() {
        // Test a journey where both stations are on the same line
        List<String> journey = tubeSystem.calculateJourney("oxford circus", "bond street");
        
        assertNotNull("Journey should not be null", journey);
        assertEquals("Journey should contain expected number of stations", 2, journey.size());
        assertEquals("Journey should start with oxford circus", "oxford circus", journey.get(0));
        assertEquals("Journey should end with bond street", "bond street", journey.get(1));
    }
    
    @Test
    public void testCalculateJourney_SameLineReversedJourney() {
        // Test a journey where both stations are on the same line but in reverse order
        List<String> journey = tubeSystem.calculateJourney("bond street", "oxford circus");
        
        assertNotNull("Journey should not be null", journey);
        assertEquals("Journey should contain expected number of stations", 2, journey.size());
        assertEquals("Journey should start with bond street", "bond street", journey.get(0));
        assertEquals("Journey should end with oxford circus", "oxford circus", journey.get(1));
    }
    
    @Test
    public void testCalculateJourney_SameLineMultipleStationsJourney() {
        // Test a journey with multiple stations on the same line
        List<String> journey = tubeSystem.calculateJourney("baker street", "paddington");
        
        assertNotNull("Journey should not be null", journey);
        assertTrue("Journey should contain at least 2 stations", journey.size() >= 2);
        assertEquals("Journey should start with baker street", "baker street", journey.get(0));
        assertEquals("Journey should end with paddington", "paddington", journey.get(journey.size() - 1));
    }
    
    @Test
    public void testCalculateJourney_OneChangeRequired() {
        // Test a journey requiring one line change
        // Victoria Line to Central Line via Oxford Circus
        List<String> journey = tubeSystem.calculateJourney("green park", "bond street");
        
        assertNotNull("Journey should not be null", journey);
        assertTrue("Journey should contain at least 3 stations", journey.size() >= 3);
        assertEquals("Journey should start with green park", "green park", journey.get(0));
        assertEquals("Journey should end with bond street", "bond street", journey.get(journey.size() - 1));
        
        // Check that the journey includes a valid transfer station
        // In this case, it would be "oxford circus" as this is a station on both lines
        assertTrue("Journey should contain the transfer station oxford circus", 
                  journey.contains("oxford circus"));
    }
    
    @Test
    public void testCalculateJourney_LongerJourney() {
        // Test a longer journey across the network
        List<String> journey = tubeSystem.calculateJourney("euston", "notting hill gate");
        
        assertNotNull("Journey should not be null", journey);
        assertTrue("Journey should have stations", journey.size() > 0);
        assertEquals("Journey should start with euston", "euston", journey.get(0));
        assertEquals("Journey should end with notting hill gate", "notting hill gate", 
                    journey.get(journey.size() - 1));
    }
    
    @Test
    public void testCalculateJourney_CaseInsensitivity() {
        // Test that station names are case-insensitive
        List<String> journey1 = tubeSystem.calculateJourney("oxford circus", "bond street");
        List<String> journey2 = tubeSystem.calculateJourney("Oxford Circus", "Bond Street");
        
        assertNotNull("Journey1 should not be null", journey1);
        assertNotNull("Journey2 should not be null", journey2);
        
        // The journeys should be the same regardless of case
        // This test assumes the TubeSystem class handles case-insensitivity properly
        assertEquals("Journeys should have the same number of stations regardless of case",
                    journey1.size(), journey2.size());
    }
    
    @Test
    public void testCalculateJourney_NonExistentStation() {
        // Test behavior when one station doesn't exist
        // This test verifies the system handles invalid input gracefully
        List<String> journey = tubeSystem.calculateJourney("oxford circus", "fake station");
        
        // Depending on how TubeSystem is implemented, it might return null, an empty list,
        // or throw an exception for non-existent stations
        if (journey != null) {
            assertTrue("Journey with non-existent station should be empty", journey.isEmpty());
        }
    }
    
    @Test
    public void testCalculateJourney_SameStation() {
        // Test behavior when start and end stations are the same
        List<String> journey = tubeSystem.calculateJourney("oxford circus", "oxford circus");
        
        assertNotNull("Journey should not be null", journey);
        assertEquals("Journey to same station should contain only one station", 1, journey.size());
        assertEquals("Journey should only contain oxford circus", "oxford circus", journey.get(0));
    }
    
    @Test
    public void testCalculateJourney_StationsOnManyLines() {
        // Test a journey between stations that appear on multiple lines
        // This tests the system's ability to find the most efficient route
        List<String> journey = tubeSystem.calculateJourney("baker street", "oxford circus");
        
        assertNotNull("Journey should not be null", journey);
        assertTrue("Journey should be direct (2 stations)", journey.size() == 2);
        assertEquals("Journey should start with baker street", "baker street", journey.get(0));
        assertEquals("Journey should end with oxford circus", "oxford circus", journey.get(1));
    }
    
    @Test(expected = NullPointerException.class)
    public void testCalculateJourney_NullParameters() {
        // Test behavior when null parameters are provided
        // This assumes the method throws NullPointerException for null inputs
        tubeSystem.calculateJourney(null, null);
    }
}