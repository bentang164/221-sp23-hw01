package edu.macalester.comp221.hw1;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class Sobel {
    private static final int[][] X_FILTER = {{-1, 0, 1}, 
                                             {-2, 0, 2}, 
                                             {-1, 0, 1}};

    private static final int[][] Y_FILTER = {{1, 2, 1}, 
                                             {0, 0, 0}, 
                                             {-1, -2, -1}};

    private BufferedImage source;
    private BufferedImage output;
    
    public Sobel() {
        source = output = null;
    }

    /**
     * Loads the image specified by the path and sets the output image to have properties identical to the input
     * Throws an exception if the specified path is not valid or if no such file exists.
     * @param path
     */
    private void loadImage(String path) {
        try {
            source = ImageIO.read(new File(path));
            output = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        } catch (IOException e) {
            System.out.println("Exception reading specified source file");
            e.printStackTrace();
        }
    }

    /**
     * Saves the modified image to disk.
     * Throws an exception if the specified path is not valid or inaccessible.
     * @param path
     */
    private void saveImage(String path) {
        try {
            File outputFile = new File("/Users/ben/Documents/ide-workspace/Java/Algorithms/221-sp23-hw01/res/output.jpg");
            ImageIO.write(output, "jpg", outputFile);
        } catch (IOException e) {
            System.out.println("Exception saving specified output file");
            e.printStackTrace();
        }
    }

    /**
     * Gets the individual RGB values at some point x, y in the loaded source image and returns them in an ArrayList. Example: [255, 255, 0]
     * @param input
     * @param x
     * @param y
     * @return
     */
    private List<Integer> getRGB(int x, int y) {
        List<Integer> rgbArray = new ArrayList<>();
        Color imageRGB = new Color(source.getRGB(x, y));

        rgbArray.add(imageRGB.getRed());
        rgbArray.add(imageRGB.getGreen());
        rgbArray.add(imageRGB.getBlue());

        return rgbArray;
    }

    /**
     * Sets the RGB values at the specified point x, y in the output image to G.
     * @param G
     * @param x
     * @param y
     */
    private void setRGB(int G, int x, int y) {
        output.setRGB(x, y, G);
    }

    /**
     * Applies the Sobel filter. 
     */
    private void applyFilter() {
        // For each pixel along the x-axis of the input:
        for (int x = 1; x < source.getWidth() - 1; x++) {
            // For each pixel along the y-axis of the input:
            for (int y = 1; y < source.getHeight() - 1; y++) {
                int Gx = 0;
                int Gy = 0;
                int G = 0;

                Map<String, List<Integer>> preFilterRGB = new HashMap<>();

                // Populates map with the RGB values for some certain point (x, y) and its neighbors
                populateMap(preFilterRGB, x, y);

                // Creates two new Maps assigned to a copy of the pre-filtered RGB values so that we don't have to repopulate when applying a different filter
                Map<String, List<Integer>> xFilterRGB = preFilterRGB;
                Map<String, List<Integer>> yFilterRGB = preFilterRGB;

                // For each neighboring pixel to center, apply the x-filter
                for (String location : xFilterRGB.keySet()) {
                    int filterIndexX = 0;
                    int filterIndexY = 0;
                    
                    // Assigns perLocationRGB to point to the current ArrayList containing RGB values for the current point
                    List<Integer> perLocationRGB = xFilterRGB.get(location);

                    // For each individual pixel in the current coordinates
                    for (int index = 0; index < 3; index++) {
                        // Update the per-pixel RGB values by multiplying them by each corresponding value in the x-filter
                        perLocationRGB.set(index, perLocationRGB.get(index) * X_FILTER[filterIndexX][filterIndexY]);
                    }

                    // If filterIndexX is less than two, we aren't at the end of the filter and can thus increment the counter to move to the next index
                    // Else if filterIndexY is less than two, we're at the end of the filter and should move to the next row by incrementing the filterIndexY 
                    // counter and resetting filterIndexX to zero
                    // If both filterIndexX and filterIndexY are greater than or equal to two, we're done with the filter and shouldn't do anything.
                    if (filterIndexX < 2) {
                        filterIndexX++;
                    } else if (filterIndexY < 2) {
                        filterIndexX = 0;
                        filterIndexY++;
                    }
                }

                // For each ArrayList in the Map of x-filtered RGB values
                for (String location: xFilterRGB.keySet()) {
                    // For each RGB channel in the current ArrayList 
                    for (int color : xFilterRGB.get(location)) {
                        // Sum into Gx
                        Gx += color;
                    }
                }

                // Take the average of the summed values: Knowing that each ArrayList contains exactly three values, we get the total by getting
                // the number of ArrayLists in the Map and then multiplying it by three
                Gx /= xFilterRGB.size() * 3;

                for (String location : yFilterRGB.keySet()) {
                    int filterIndexX = 0;
                    int filterIndexY = 0;
                    
                    List<Integer> perLocationRGB = yFilterRGB.get(location);

                    for (int index = 0; index < 3; index++) {
                        perLocationRGB.set(index, perLocationRGB.get(index) * Y_FILTER[filterIndexX][filterIndexY]);
                    }

                    if (filterIndexX < 2) {
                        filterIndexX++;
                    } else if (filterIndexY < 2) {
                        filterIndexX = 0;
                        filterIndexY++;
                    }
                }

                for (String location: yFilterRGB.keySet()) {
                    for (int color : yFilterRGB.get(location)) {
                        Gy += color;
                    }
                }

                Gy /= xFilterRGB.size() * 3;

                // Use the Sobel formula to determine what the RGB values for the current pixel should be set to in the output image
                G = (int) Math.sqrt(Math.pow(Gx, 2) + Math.pow(Gy, 2));

                // Set the RGB values for the current pixel
                setRGB(G, x, y);

                // Clear all of the Maps to prepare for the next iteration
                preFilterRGB.clear();
                xFilterRGB.clear();
                yFilterRGB.clear();
            }
        }
    }

    /**
     * Populates a Map with pre-filtered RGB values for some point (x, y) and its neighbors in a 3x3 grid
     * @param preFilterRGB
     * @param x coordinate
     * @param y coordinate
     */
    private void populateMap(Map<String, List<Integer>> preFilterRGB, int x, int y) {
        preFilterRGB.put("upperLeft", getRGB(x-1, y-1));
        preFilterRGB.put("upperCenter", getRGB(x, y-1));
        preFilterRGB.put("upperRight", getRGB(x+1, y-1));
        preFilterRGB.put("centerLeft", getRGB(x-1, y));
        preFilterRGB.put("center", getRGB(x, y));
        preFilterRGB.put("centerRight", getRGB(x+1, y));
        preFilterRGB.put("lowerLeft", getRGB(x-1, y+1));
        preFilterRGB.put("lowerCenter", getRGB(x, y+1));
        preFilterRGB.put("lowerRight", getRGB(x+1, y+1));
    }

    public static void main(String[] args) {
        Sobel sobel = new Sobel();

        sobel.loadImage("/Users/ben/Documents/ide-workspace/Java/Algorithms/221-sp23-hw01/res/source.jpg");

        sobel.applyFilter();

        sobel.saveImage("/Users/ben/Documents/ide-workspace/Java/Algorithms/221-sp23-hw01/res/output.jpg");
    }
}
