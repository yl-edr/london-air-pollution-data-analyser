import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;

public class MapImage {
    private Image image;

    public MapImage(String fileName) {
        try {
            FileInputStream input = new FileInputStream(fileName);
            image = new Image(input);
        } catch (FileNotFoundException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
    }

    public Image getImage() {
        return image;
    }

    public void makePixelRedder(int x, int y) {
        int colour = image.getPixelReader().getArgb(x, y);
        System.out.println("Colour: " + colour);
    }
}
