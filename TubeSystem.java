import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TubeSystem {
    private TubeLine bakerloo = new TubeLine("Bakerloo");
    private TubeLine central = new TubeLine("Central");
    private TubeLine circle = new TubeLine("Circle");
    private TubeLine district = new TubeLine("District");
    private TubeLine hammersmithAndCity = new TubeLine("Hammersmith & City");
    private TubeLine jubilee = new TubeLine("Jubilee");
    private TubeLine metropolitan = new TubeLine("Metropolitan");
    private TubeLine northernBB = new TubeLine("Northern (via Bank)");
    private TubeLine northernCCB = new TubeLine("Northern (via Charing Cross)");
    private TubeLine piccadilly = new TubeLine("Piccadilly");
    private TubeLine victoria = new TubeLine("Victoria");
    private TubeLine waterlooAndCity = new TubeLine("Waterloo & City");
    private TubeLine elizabethLine = new TubeLine("Elizabeth Line");
    private ArrayList <String> journey= new ArrayList<>();
    private ArrayList <TubeLine> tubeLines = new ArrayList<>();
    private HashMap<String, List <TubeLine>> hubStations = new HashMap<>();

    public TubeSystem(){
        String[] bakerlooStations = {"elephant & castle", "lambeth north", "waterloo", "embankment", "charing cross", "piccadilly circus", "oxford circus", "regents park", "baker street", "marleybone", "edgware road", "paddington"};
        bakerloo.addStations(bakerlooStations);

        String[] centralStations = {"liverpool street", "bank", "st pauls", "chancery lane", "holborn", "tottenham court road", "oxford circus", "bond street", "marble arch", "lancaster gate", "queensway", "notting hill gate"};
        central.addStations(centralStations);

        String[] hammersmithAndCityStations = {"aldgate east", "liverpool street", "moorgate", "barbican", "farringdon", "kings cross", "euston square", "great portland street", "baker street", "edgware road", "paddington"};
        hammersmithAndCity.addStations(hammersmithAndCityStations);

        String[] circleStations = {"edgware road", "baker street", "great portland street", "euston square", "king's cross", "farringdon", "barbican", "moorgate", "liverpool street", "aldgate", "tower hill", "monument", "cannon street", "mansion house", "blackfriars", "temple", "embankment", "westminster", "st. james's park", "victoria", "sloane square", "south kensington", "gloucester road", "high street kensington", "notting hill gate", "bayswater", "paddington"};
        circle.addStations(circleStations);

        String[] districtStations = {"aldgate east", "tower hill", "monument", "cannon street", "mansion house", "blackfriars", "temple", "embankment", "westminster", "st. james's park", "victoria", "sloane square", "south kensington", "gloucester road", "high street kensington", "notting hill gate", "bayswater", "paddington", "earl's court"};
        district.addStations(districtStations);

        String[] jubileeStations = {"london bridge", "southwark", "waterloo", "westminster", "green park", "bond street",  "baker street"};
        jubilee.addStations(jubileeStations);

        String[] metropolitanStations = {"aldgate", "liverpool street", "moorgate", "barbican", "farringdon", "kings cross", "euston square", "great portland street", "baker street"};
        metropolitan.addStations(metropolitanStations);

        String[] northernStationsBB = {"euston", "kings cross", "angel", "old street", "moorgate", "bank", "london bridge", "borough", "elephant & castle", "kennington"};
        String[] northernStationsCCB = {"euston", "warren street", "goodge street", "tottenham court road", "leicester square", "charing cross", "embankment", "waterloo", "kennington", "nine elms", "battersea power station"};
        northernBB.addStations(northernStationsBB);
        northernCCB.addStations(northernStationsCCB);

        String[] piccadillyStations = {"kings cross", "russell square", "holborn", "covent garden", "leicester square", "piccadilly circus", "green park", "hyde park corner", "knightsbridge", "south kensington", "gloucester road", "earls court"};
        piccadilly.addStations(piccadillyStations);

        String[] victoriaStations = {"kings cross", "euston", "warren street", "oxford circus", "green park", "victoria", "pimlico", "vauxhall"};
        victoria.addStations(victoriaStations);

        String[] waterlooAndCityStations = {"bank", "waterloo"};
        waterlooAndCity.addStations(waterlooAndCityStations);

        String[] elizabethLineStations = {"liverpool street", "farringdon", "tottenham court road", "bond street", "paddington"};
        elizabethLine.addStations(elizabethLineStations);

        tubeLines.add(bakerloo);
        tubeLines.add(central);
        tubeLines.add(hammersmithAndCity);
        tubeLines.add(circle);
        tubeLines.add(district);
        tubeLines.add(jubilee);
        tubeLines.add(metropolitan);
        tubeLines.add(northernBB);
        tubeLines.add(northernCCB);
        tubeLines.add(piccadilly);
        tubeLines.add(victoria);
        tubeLines.add(waterlooAndCity);
        tubeLines.add(elizabethLine);
    }

    public List<String> calculateJourney(String start, String finish){
        List<String> journey = bothStationsOnLine(start,finish);
        if(journey!=null){
            return journey;
        }
        return changeLines(start, finish);
    }


    private List<String> bothStationsOnLine(String station1, String station2) {
        for (TubeLine line : tubeLines){
            if (line.isStationOnLine(station1) && line.isStationOnLine(station2)){
                if(line.getStations().indexOf(station1)< line.getStations().indexOf(station2)+1){
                    return line.getStations().subList(line.getStations().indexOf(station1), line.getStations().indexOf(station2)+1);
                }
                List<String> journey = line.getStations().subList(line.getStations().indexOf(station2), line.getStations().indexOf(station1)+1);
                List<String> reversedJourney = new ArrayList<>();
                for (int i = journey.size() - 1; i >= 0; i--) {
                    reversedJourney.add(journey.get(i));
                }
                return reversedJourney;
            }
        } 
        return null;  
    }

    private List<String> changeLines(String start, String end){
        ArrayList<TubeLine> startLines = new ArrayList<>();
        ArrayList<TubeLine> endLines = new ArrayList<>();
        HashMap<Integer, List<String>> journeyLength = new HashMap<>();
        for(TubeLine line : tubeLines){
            if(line.isStationOnLine(start)){
                startLines.add(line);
            }
            if(line.isStationOnLine(end)){
                endLines.add(line);
            }
        }
        String changeStation;
        for(TubeLine line1 : startLines){
            for(TubeLine line2 : endLines){
                changeStation = findHub(line1, line2);
                if(changeStation!=null){
                    // System.out.println(line1);
                    // System.out.println(line2);
                    List<String> journey1 = bothStationsOnLine(start, changeStation);
                    List<String> journey2 = bothStationsOnLine(changeStation, end);
                    // System.out.println("first line "+journey1);
                    // System.out.println("second line "+journey2);
                    List<String> fullJourney= new ArrayList<>();
                    fullJourney.addAll(List.copyOf(journey1));
                    fullJourney.addAll(List.copyOf(journey2));
                    fullJourney.remove(changeStation);
                    // System.out.println(fullJourney.size());
                    // System.out.println("overall "+fullJourney);
                    journeyLength.put(fullJourney.size(), List.copyOf(fullJourney));
                }
            }
        }
        return journeyLength.get(getMinKey(journeyLength));
    }

    private String findHub(TubeLine line1, TubeLine line2){
        for(String station : line1.getStations()){
            if(line2.isStationOnLine(station)){
                return station;
            }
        }
        return null;

    }

    private int getMinKey(HashMap<Integer, List<String>> map) {
        if (map.isEmpty()) {
            return 0;
        }
        int minKey = Integer.MAX_VALUE; 
        for (int key : map.keySet()) {
            if (key < minKey) {
                minKey = key;
            }
        }
        return minKey;
    }
    
}
