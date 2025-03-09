import java.util.HashMap;
import java.io.File;

/**
 * This class is responsible for loading and storing all the available pollution data.
 */

public class DataAggregator {

    HashMap<String, DataSet> dataSets; // key is year + pollutant
    DataFilter dataFilter; // filters data to only include London data

    public DataAggregator() {
        dataSets = new HashMap<>();
        dataFilter = new DataFilter();
    }

    /**
     * Adds a new data set to the data aggregator after filtering it
     * @param dataSet the data set to add
     */
    public void addDataSet(DataSet dataSet) {
        dataSet = filterData(dataSet);
        dataSets.put(dataSet.getYear() + dataSet.getPollutant(), dataSet);
    }

    private DataSet filterData(DataSet dataSet) {
        return dataFilter.filterLondonData(dataSet);
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
     */
    public DataSet getDataSet(String year, String pollutant) {
        return dataSets.get(year + pollutant);
    }

}
