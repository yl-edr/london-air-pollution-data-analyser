import java.util.HashMap;
import java.io.File;

public class DataAggregator {

    HashMap<String, DataSet> dataSets;
    DataFilter dataFilter;

    public DataAggregator() {
        dataSets = new HashMap<>();
        dataFilter = new DataFilter();
    }

    public void addDataSet(DataSet dataSet) {
        dataSet = filterData(dataSet);
        dataSets.put(dataSet.getYear() + dataSet.getPollutant(), dataSet);
    }

    private DataSet filterData(DataSet dataSet) {
        return dataFilter.filterLondonData(dataSet);
    }

    public void processDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.endsWith(".csv")) {
                    DataSet dataSet = new DataLoader().loadDataFile(directoryPath + fileName);
                    addDataSet(dataSet);
                }
            }
        }
    }

    public DataSet getDataSet(String year, String pollutant) {
        return dataSets.get(year + pollutant);
    }

}
