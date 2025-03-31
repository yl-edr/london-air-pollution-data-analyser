import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class ApiConnection {

    private static final String apiKey = "42912c00d47c0fb3ae19bcb050db29c5";

    public ApiConnection() {
    }

    public HashMap<String, double[]> getTopLocationsForSearch(String location) {
        String locationJson = makeApiCallToGeolocatorAPI(location);
        if (locationJson == null) {
            System.out.println("Failed to retrieve location data.");
            return null;
        }
        return extractTopMatches(locationJson);
    }

    public double[] getDataForLatLon(double lat, double lon){
        String airPollutionJson = makeApiCallToLocation(lat, lon);
        if (airPollutionJson == null) {
            System.out.println("Failed to retrieve air pollution data.");
            return null;
        }
        return processDataJsonString(airPollutionJson);
    }

    private String makeApiCallToLocation(double lat, double lon) {
        String urlString = "https://api.openweathermap.org/data/2.5/air_pollution?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
            } else {
                System.out.println("API request failed. Response Code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            System.out.println("API call failed for lat=" + lat + ", lon=" + lon);
            return null;
        }
    }

    private String makeApiCallToGeolocatorAPI(String location) {
        location = location.replace(" ", "%20"); // deal with location names with spaces e.g. los angeles
        String urlString = "http://api.openweathermap.org/geo/1.0/direct?q=" + location + "&limit=8&appid=" + apiKey;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
            } else {
                System.out.println("API request failed. Response Code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            System.out.println("API call failed for location=" + location);
            return null;
        }
    }

    private double[] processDataJsonString(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);

            JsonNode listNode = rootNode.path("list").get(0);
            JsonNode mainNode = listNode.path("main");
            JsonNode componentsNode = listNode.path("components");

            double aqi = mainNode.path("aqi").asDouble();
            double co = componentsNode.path("co").asDouble();
            double no = componentsNode.path("no").asDouble();
            double no2 = componentsNode.path("no2").asDouble();
            double o3 = componentsNode.path("o3").asDouble();
            double so2 = componentsNode.path("so2").asDouble();
            double pm2_5 = componentsNode.path("pm2_5").asDouble();
            double pm10 = componentsNode.path("pm10").asDouble();
            double nh3 = componentsNode.path("nh3").asDouble();

            return new double[]{aqi, co, no2, o3, so2, pm2_5, pm10, nh3}; // NO is excluded here because some issue with the API seems to always return NO as 0
        } catch (IOException e) {
            System.out.println("Error processing JSON: " + e.getMessage());
        }
        return null;
    }

    private HashMap<String, double[]> extractTopMatches(String jsonString) {
        HashMap<String, double[]> matches = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            if (rootNode.isArray() && rootNode.size() > 0) {
                for (int i = 0; i < rootNode.size(); i++) {
                    JsonNode locationNode = rootNode.get(i);
                    String name = locationNode.path("name").asText();
                    String country = locationNode.path("country").asText();
                    String state = locationNode.has("state") ? locationNode.path("state").asText() : "";
                    String matchName = name + (state.isEmpty() ? "" : ", " + state) + ", " + country;
                    double lat = locationNode.path("lat").asDouble();
                    double lon = locationNode.path("lon").asDouble();
                    matches.put(matchName, new double[]{lat, lon});
                }
                return matches;
            } else {
                System.out.println("No location data found in JSON.");
            }
        } catch (IOException e) {
            System.out.println("Error processing JSON: " + e.getMessage());
        }
        return null;
    }

}


