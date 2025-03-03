import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

public class MapImage {
    private Image image;
    private WritableImage writableImage;

    public MapImage(String fileName) {
        try {
            FileInputStream input = new FileInputStream(fileName);
            image = new Image(input);

            writableImage = new WritableImage(
                    (int)image.getWidth(),
                    (int)image.getHeight()
            );

            PixelReader reader = image.getPixelReader();
            PixelWriter writer = writableImage.getPixelWriter();
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    writer.setArgb(x, y, reader.getArgb(x, y));
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
    }

    public Image getImage() {
        return writableImage;
    }

    public int getPixel(int x, int y) {
        PixelReader reader = writableImage.getPixelReader();
        return reader.getArgb(x, y);
    }

    public void setPixel(int x, int y, int argb) {
        PixelWriter writer = writableImage.getPixelWriter();
        writer.setArgb(x, y, argb);
    }

    private void makePixelRedder(int x, int y, double percentage){
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = writableImage.getPixelWriter();
    }

    public void testPixelAltering() {
        for (int y = 0; y < writableImage.getHeight(); y++) {
            for (int x = 0; x < writableImage.getWidth(); x++) {
                int argb = getPixel(x, y);
                argb = (argb & 0xFF00FF00) | 0x000000FF;
                setPixel(x, y, argb);
            }
        }

    }
    }

