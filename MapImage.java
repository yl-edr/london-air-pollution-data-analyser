import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

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

    private void makePixelRedder(int x, int y, double percentage){
        int[] rgb = getPixel(x, y);
        int red = Math.min(255, (int)(rgb[0] * (1 + percentage)));
        int green = rgb[1];
        int blue = rgb[2];
        int argb = (0xFF << 24) | (red << 16) | (green << 8) | blue;
        writableImage.getPixelWriter().setArgb(x, y, argb);
    }

    public void testPixelAltering() {
        for (int y = 0; y < writableImage.getHeight() - 500; y++) {
            for (int x = 0; x < writableImage.getWidth() - 400; x++) {
                makePixelRedder(x, y, 0.9);
            }
        }
        for (int y = 0; y < writableImage.getHeight() - 300; y++) {
            for (int x = 0; x < writableImage.getWidth() - 300; x++) {
                makePixelRedder(x, y, 0.7);
            }
        }
        for (int y = 0; y < writableImage.getHeight() - 200; y++) {
            for (int x = 0; x < writableImage.getWidth() - 100; x++) {
                makePixelRedder(x, y, 0.5);
            }
        }

    }

    public int[] getPixel(int x, int y) {
        PixelReader reader = writableImage.getPixelReader();
        int argb = reader.getArgb(x, y);
        int[] rgb = new int[3];
        rgb[0] = (argb >> 16) & 0xFF;
        rgb[1] = (argb >> 8) & 0xFF;
        rgb[2] = argb & 0xFF;
        return rgb;
    }
    }

