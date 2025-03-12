import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;


public class ApiConnection {

    ArrayList<List<String>> locations;

    private static final String apiKey = "42912c00d47c0fb3ae19bcb050db29c5";

    public ApiConnection() {
    }

    public void parseConversionCSV() {
        ArrayList<List<String>> locations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("resources/EastingNorthingToLatLong.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                locations.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
        this.locations = locations;
    }

    public ArrayList<List<String>> getLocations() {
        return locations;
    }

    public boolean canMakeFullDataSetRequest() {
        long currentEpoch = Instant.now().getEpochSecond();
        File lastRequestFile = new File("resources/lastRequest.txt");
        long lastRequestEpoch = 0;
        try {

            Scanner scanner = new Scanner(lastRequestFile);
            lastRequestEpoch = scanner.nextLong();
            scanner.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
        long elapsedTime = currentEpoch - lastRequestEpoch;
        if (elapsedTime > 30) {
            try{
                PrintWriter writer = new PrintWriter("resources/lastRequest.txt");
                writer.println(currentEpoch);
                writer.close();
                return true;
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        }
        return false;
    }

    public HashMap<String, DataSet> updateDataSet(){
        HashMap<String, DataSet> allData = new HashMap<>();
        allData.put("AQI", new DataSet("AQI", "2021", "liveData", ""));
        allData.put("CO", new DataSet("CO", "2021", "liveData", "ug m-3"));
        allData.put("NO", new DataSet("NO", "2021", "liveData", "ug m-3"));
        allData.put("NO2", new DataSet("NO2", "2021", "liveData", "ug m-3"));
        allData.put("O3", new DataSet("O3", "2021", "liveData", "ug m-3"));
        allData.put("SO2", new DataSet("SO2", "2021", "liveData", "ug m-3"));
        allData.put("PM2.5", new DataSet("PM2.5", "2021", "liveData", "ug m-3"));
        allData.put("PM10", new DataSet("PM10", "2021", "liveData", "ug m-3"));
        allData.put("NH3", new DataSet("NH3", "2021", "liveData", "ug m-3"));
        for (List<String> location : locations) {
            int gridCode = Integer.parseInt(location.get(0));
            int easting = Integer.parseInt(location.get(1));
            int northing = Integer.parseInt(location.get(2));
            double lat = Double.parseDouble(location.get(4));
            double lon = Double.parseDouble(location.get(5));
            String returnedDataJSON = makeApiCallToLocation(lat, lon);
            double[] data;
            if (returnedDataJSON != null) {
                data = processJsonString(returnedDataJSON);
                System.out.println("Processed data for grid code " + gridCode);
                allData.get("AQI").addData(new String[]{Integer.toString(gridCode), Integer.toString(easting), Integer.toString(northing), Double.toString(data[0])});
                allData.get("CO").addData(new String[]{Integer.toString(gridCode), Integer.toString(easting), Integer.toString(northing), Double.toString(data[1])});
                allData.get("NO").addData(new String[]{Integer.toString(gridCode), Integer.toString(easting), Integer.toString(northing), Double.toString(data[2])});
                allData.get("NO2").addData(new String[]{Integer.toString(gridCode), Integer.toString(easting), Integer.toString(northing), Double.toString(data[3])});
                allData.get("O3").addData(new String[]{Integer.toString(gridCode), Integer.toString(easting), Integer.toString(northing), Double.toString(data[4])});
                allData.get("SO2").addData(new String[]{Integer.toString(gridCode), Integer.toString(easting), Integer.toString(northing), Double.toString(data[5])});
                allData.get("PM2.5").addData(new String[]{Integer.toString(gridCode), Integer.toString(easting), Integer.toString(northing), Double.toString(data[6])});
                allData.get("PM10").addData(new String[]{Integer.toString(gridCode), Integer.toString(easting), Integer.toString(northing), Double.toString(data[7])});
                allData.get("NH3").addData(new String[]{Integer.toString(gridCode), Integer.toString(easting), Integer.toString(northing), Double.toString(data[8])});
            }
        }
        return allData;
    }

    private String makeApiCallToLocation(double lat, double lon) {
        String urlString = "https://api.openweathermap.org/data/2.5/air_pollution?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("API request successful for lat=" + lat + ", lon=" + lon);
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

    private double[] processJsonString(String jsonString) {
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

            return new double[]{aqi, co, no, no2, o3, so2, pm2_5, pm10, nh3};
        } catch (IOException e) {
            System.out.println("Error processing JSON: " + e.getMessage());
        }
        return null;
    }

}

