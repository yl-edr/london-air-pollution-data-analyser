public class CityData {
    private String name;
    private double[] data;

    public CityData(String name, double[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public double[] getData() {
        return data;
    }
}
