import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class MapImage {
    private Image baseImage;
    private WritableImage colourImage;
    private double blendAplha = 0.5;

    public MapImage(String fileName) {
        try {
            FileInputStream input = new FileInputStream(fileName);
            baseImage = new Image(input);

            colourImage = new WritableImage(
                    (int)baseImage.getWidth(),
                    (int)baseImage.getHeight()
            );

            PixelWriter writer = colourImage.getPixelWriter();
            int blankArgb = 0xFF << 24;
            for (int y = 0; y < baseImage.getHeight(); y++) {
                for (int x = 0; x < baseImage.getWidth(); x++) {
                    writer.setArgb(x, y, blankArgb);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
    }

    public Image getImage() {
        return baseImage;
    }


    /**
     * Place a colour block on the overlay colour image at full opacity.
     * @param startX The x-coordinate of the top-left corner of the block
     * @param startY The y-coordinate of the top-left corner of the block
     * @param width The width of the block
     * @param height The height of the block
     * @param dataPercentage The data points location in the colour spectrum as a percentage relative to min and max values. Used tio determine the colour of the block.
     */
    private void placeOverlayBlock(int startX, int startY, int width, int height, double dataPercentage) {
        PixelWriter writer = colourImage.getPixelWriter();
        int alpha = 255;
        int green = (int)(255 * (1 - dataPercentage));
        int red = (int)(255 * dataPercentage);
        int blue = 0;
        int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                writer.setArgb(x, y, argb);
            }
        }
    }

    public void testPixelAltering() {
        placeOverlayBlock(0, 0, 100, 100, 0.0);
        placeOverlayBlock(100, 100, 100, 100, 0.1);
        placeOverlayBlock(200, 200, 100, 100, 0.2);
        placeOverlayBlock(300, 300, 100, 100, 0.3);
        placeOverlayBlock(400, 400, 100, 100, 0.4);
        placeOverlayBlock(500, 500, 100, 100, 0.5);
        placeOverlayBlock(600, 600, 100, 100, 0.6);
        placeOverlayBlock(700, 700, 100, 100, 0.7);
        placeOverlayBlock(800, 800, 100, 100, 0.8);
        placeOverlayBlock(900, 900, 100, 100, 0.9);
        placeOverlayBlock(1000, 1000, 100, 100, 1.0);
    }

    public int[] getPixel(int x, int y) {
        PixelReader reader = colourImage.getPixelReader();
        int argb = reader.getArgb(x, y);
        int[] rgb = new int[3];
        rgb[0] = (argb >> 16) & 0xFF;
        rgb[1] = (argb >> 8) & 0xFF;
        rgb[2] = argb & 0xFF;
        return rgb;
    }

    public Image getCombined() {

        WritableImage newImage = new WritableImage((int) baseImage.getWidth(), (int) baseImage.getHeight());
        int width = (int) Math.min(baseImage.getWidth(), colourImage.getWidth());
        int height = (int) Math.min(baseImage.getHeight(), colourImage.getHeight());

        PixelReader overlayReader = colourImage.getPixelReader();
        PixelReader baseReader = baseImage.getPixelReader();
        PixelWriter writer = newImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int baseArgb = baseReader.getArgb(x, y);
                int overlayArgb = overlayReader.getArgb(x, y);

                int baseRed = (baseArgb >> 16) & 0xFF;
                int baseGreen = (baseArgb >> 8) & 0xFF;
                int baseBlue = baseArgb & 0xFF;

                int overlayRed = (overlayArgb >> 16) & 0xFF;
                int overlayGreen = (overlayArgb >> 8) & 0xFF;
                int overlayBlue = overlayArgb & 0xFF;

                int newRed = (int) (baseRed * (1 - blendAplha) + overlayRed * blendAplha);
                int newGreen = (int) (baseGreen * (1 - blendAplha) + overlayGreen * blendAplha);
                int newBlue = (int) (baseBlue * (1 - blendAplha) + overlayBlue * blendAplha);

                newRed = Math.min(255, Math.max(0, newRed));
                newGreen = Math.min(255, Math.max(0, newGreen));
                newBlue = Math.min(255, Math.max(0, newBlue));

                int newArgb = (0xFF << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                writer.setArgb(x, y, newArgb);
            }
        }

        return newImage;
    }
    }

