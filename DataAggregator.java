import java.util.HashMap;
import java.io.File;

/**
 * This class is responsible for loading and storing all the available pollution data.
 */

public class DataAggregator {

    HashMap<String, DataSet> dataSets; // key is year + pollutant
    DataFilter dataFilter; // filters data to only include London data
    HashMap<String, DataSet> citiesDataSets;
    TubeDataSet tubeDataSet;

    public DataAggregator() {
        dataSets = new HashMap<>();
        dataFilter = new DataFilter();
        citiesDataSets = new HashMap<>();
    }

    public void addDataSet(DataSet dataSet) {
        HashMap<String, DataSet> filteredDataSets = DataFilter.filterCityData(dataSet);

        for (String city : filteredDataSets.keySet()) {
            DataSet cityData = filteredDataSets.get(city);
            String key = generateKey(city, cityData.getYear(), cityData.getPollutant());
            citiesDataSets.put(key, cityData);
        }
    }

    public void addDataSet(TubeDataSet dataSet) {
        tubeDataSet = dataSet;
    }

    /*private DataSet filterData(DataSet dataSet) {

        return dataFilter.filterCityData(dataSet);
    }*/

    private String generateKey(String city, String year, String pollutant) {
        return city + "_" + year + "_" + pollutant;
    }

    /**
     * Processes all the csv files in a given directory. This structure allows for another pollutant to be added easily in the future.
     * @param directoryPath
     */
    public void processDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                // only process csv files
                if (fileName.endsWith(".csv")) {
                        DataSet dataSet = new DataLoader().loadDataFile(directoryPath + fileName);
                        if(fileName.contains("Tube"))
                            addDataSet((TubeDataSet) dataSet);
                        else
                            addDataSet(dataSet);
                    
                }
            }
        }
    }

    /**
     * Returns a data set based on the year and pollutant
     * @param year the year of the data set
     * @param pollutant the pollutant of the data set
     * @return the data set
     *
    public DataSet getDataSet(String city, String year, String pollutant) {
        return dataSets.get(city + year + pollutant);
    }*/

    public DataSet getCityDataSet(String city, String year, String pollutant) {
        String key = generateKey(city, year, pollutant);
        return citiesDataSets.get(key);
    }

    public TubeDataSet getTubeDataSet() {
        return tubeDataSet;
    }
}
