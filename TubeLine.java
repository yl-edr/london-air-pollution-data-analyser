import java.util.ArrayList;

public class TubeLine {
    private String name;
    private ArrayList<String> stations;
    public TubeLine(String name) {
        this.name = name;
        stations = new ArrayList<>();
    }
    public void addStation(String station) {
        stations.add(station);
    }
    public void addStations(String[] stations) {
        for (String station : stations) {
            addStation(station);
        }
    }
    public String getName() {
        return name;
    }
    public boolean isStationOnLine(String station) {
        return stations.contains(station);
    }
    
    public ArrayList<String> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return "This is the " + name + " line";
    }
}
