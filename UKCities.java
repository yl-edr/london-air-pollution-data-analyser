import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;

public class UKCities {
    
    private ComboBox<String> cityComboBox;
    private DataAggregator dataAggregator;
    
    public UKCities(String cityName, DataAggregator dataAggregator) {
        //super(cityName,new int[]{317339, 331640, 668176 ,676443, 3}, dataAggregator);
        this.dataAggregator = dataAggregator;
        //createCitySelector();
    }
    public UKCities(String cityName, int[] bounds, DataAggregator dataAggregator) {
        //super(cityName,bounds, dataAggregator);
        this.dataAggregator = dataAggregator;
        //createCitySelector();
    }
    
    /*public void createCitySelector() {
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
    
    /*private void updateCity(String cityName) {
        int[] manchesterBounds = {376000, 390901, 401667, 393400, 3};
        int[] edinburghBounds = {317339, 331640, 668176 ,676443, 3};
        //System.out.println("City selected: " + cityName);
        switch (cityName) {
            case "Manchester":
                setBounds(manchesterBounds);
                this.name = "Manchester";  // Update city name
                System.out.println("City selected: " + cityName);    
                break;
            case "Edinburgh":
                setBounds(edinburghBounds);
                this.name = "Edinburgh";
                break;
        
            default:
                break;
        }
        new UKCities(name, dataAggregator);
        create(name);
        updateColourMap();
        
        AppWindow.setUKCities(this);
        
    }*/
}
