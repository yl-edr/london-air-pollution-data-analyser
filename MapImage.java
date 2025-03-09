import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Holds the map image and has methods to add coloured data points to it
 * @author Anton Davidouski
 * @version 1.0
 */

public class MapImage {
    private Image baseImage; //  will hold the map png
    private WritableImage colourImage; //  will hold a png of coloured blocks representing pollution data
    private double blendAplha = 0.35; // will dwtermine blend ration between base and colour image


    // Real life Northings and Eastings for given image
    private static final int MIN_X = 510394;
    private static final int MAX_X = 553297;
    private static final int MIN_Y = 168504;
    private static final int MAX_Y = 193305;

    public MapImage(String fileName) {
        try {
            FileInputStream input = new FileInputStream(fileName);
            baseImage = new Image(input);

            // make a duplicate blank image with same dimensions as base image
            colourImage = new WritableImage(
                    (int)baseImage.getWidth(),
                    (int)baseImage.getHeight()
            );

            PixelWriter writer = colourImage.getPixelWriter();
            int blankArgb = 0xFF << 24; // ARGB is a 32-bit integer, with 8 bits for each of the four components (alpha, r, g, b), so bitshift FF by 24 to set alpha to full and set the rest to 0
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
     * Processes a single data point and place a coloured block on the overlay image, based on the data point's value.
     * @param dataPoint the dataPoint object to process
     * @param min the minimum value in the dataset
     * @param max the maximum value in the dataset
     */
    public void processDataPoint(DataPoint dataPoint, double min, double max) {
        double dataPercentage = (dataPoint.value() - min) / (max - min);
        int x = dataPoint.x();
        int y = dataPoint.y();

        // The following 2 lines convert the real live Norhtings and Eastings to pixel coordinates
        int imageX = (int) (colourImage.getWidth() * (x - MIN_X) / (MAX_X - MIN_X));
        int imageY = (int) (colourImage.getHeight() * (y - MIN_Y) / (MAX_Y - MIN_Y));

        // Width and height of the block to place on the overlay image - for some reason data points are more
        // vertically separated than horizontally, so the block is taller than it is wide to make colour continuous.
        // Setting height to 44 results in horizontal line of no colour across the image
        int width = 44;
        int height = 46;

        // single imageX and Y will define the top corner of the block, and we want the colour block to be centered on the data point, shift they up and left by half the width and height
        imageX -= width / 2;
        imageY -= height / 2;
        placeOverlayBlock(imageX, imageY, width, height, dataPercentage);
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
        int alpha = 255; // full opacity
        // Create blend of red and green based on data percentage
        int green = (int) (255 * (1 - dataPercentage));
        int red = (int) (255 * dataPercentage);
        int blue = 0; // no blue
        int argb = (alpha << 24) | (red << 16) | (green << 8) | blue; // combined into a single integer using bit shifting

        // set right range of pixels to the colour needed.
        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                try {
                    writer.setArgb(x, y, argb);
                } catch (IndexOutOfBoundsException e) {
                    // Ignore pixels that are outside the image - this happens when the data point is near the edge of the image
                }
            }
        }
    }


    /**
     * Combine the base image with the overlay image, creating the map with pollution data added to it.
     * @return The combined image which can be added to the IMageView
     */
    public Image getCombined() {

        // create a new image to hold the combined image, with the same dimensions as the base and colour images
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

                // Extract all components of argb values of base and overlay images.
                int baseRed = (baseArgb >> 16) & 0xFF;
                int baseGreen = (baseArgb >> 8) & 0xFF;
                int baseBlue = baseArgb & 0xFF;

                int overlayRed = (overlayArgb >> 16) & 0xFF;
                int overlayGreen = (overlayArgb >> 8) & 0xFF;
                int overlayBlue = overlayArgb & 0xFF;

                // Blend the two images together using the blend ratio. Cast back to int.
                int newRed = (int) (baseRed * (1 - blendAplha) + overlayRed * blendAplha);
                int newGreen = (int) (baseGreen * (1 - blendAplha) + overlayGreen * blendAplha);
                int newBlue = (int) (baseBlue * (1 - blendAplha) + overlayBlue * blendAplha);

                // check new values in correct range
                newRed = Math.min(255, Math.max(0, newRed));
                newGreen = Math.min(255, Math.max(0, newGreen));
                newBlue = Math.min(255, Math.max(0, newBlue));

                // combine into a single int and set to new image
                int newArgb = (0xFF << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                writer.setArgb(x, y, newArgb);
            }
        }
        return newImage;
    }
}

