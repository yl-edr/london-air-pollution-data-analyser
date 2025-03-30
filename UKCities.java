import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;

public class UKCities extends City {
    
    private ComboBox<String> cityComboBox;
    private DataAggregator dataAggregator;
    
    public UKCities(String cityName, DataAggregator dataAggregator) {
        super(cityName,new int[]{317339, 331640, 668176 ,676443, 3}, dataAggregator);
        //this.dataAggregator = dataAggregator;
        createCitySelector();
    }
    public UKCities(String cityName, int[] bounds, DataAggregator dataAggregator) {
        super(cityName,bounds, dataAggregator);
        this.dataAggregator = dataAggregator;
        createCitySelector();
    }
    
    private void createCitySelector() {
        Label cityLabel = new Label("Choose a city:");
        cityComboBox = new ComboBox<>();
        cityComboBox.setPromptText("City");
        cityComboBox.getItems().addAll("Manchester", "Edinburgh");
        GridPane.setMargin(cityComboBox, new Insets(0, 0, 10, 0));
        //getRightBar().add(cityLabel, 0, 0);
        getRightBar().add(cityComboBox, 0, 0);

        cityComboBox.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateCity(newValue);
                }
            }
        );
        
        //getPane().setTop(cityComboBox);
    }
    
    private void updateCity(String cityName) {
        int[] manchesterBounds = {376000, 390901, 401667, 393400, 3};
        int[] edinburghBounds = {317339, 331640, 668176 ,676443, 3};
        //System.out.println("City selected: " + cityName);
        switch (cityName) {
            case "Manchester":
                City ma = new UKCities(cityName, manchesterBounds, dataAggregator); 
                System.out.println("City selected: " + cityName);    
                break;
            case "Edinburgh":
                City ed = new UKCities(cityName, edinburghBounds, dataAggregator);    
                break;
        
            default:
                break;
        }
        
        
        //super.setBounds(newBounds); 
        //super.create(cityName);
        //super.mapView.setImage(super.map.getImage());
        
        //updateColourMap();
    }
}
