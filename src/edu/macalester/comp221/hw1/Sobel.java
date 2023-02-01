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
            File outputFile = new File("/res/output.png");
            ImageIO.write(output, "png", outputFile);
        } catch (IOException e) {
            System.out.println("Exception saving specified output file");
            e.printStackTrace();
        }
    }

    /**
     * Gets the individual RGB values at some point x, y in the loaded source image and returns them in an ArrayList. Example: {255, 255, 0}
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
        for (int x = 1; x < source.getWidth() - 1; x++) {
            for (int y = 1; y < source.getHeight() - 1; y++) {
                int Gx = 0;
                int Gy = 0;
                int G = 0;

                Map<String, List<Integer>> preFilterRGB = new HashMap<>();

                preFilterRGB.put("upperLeft", getRGB(x-1, y-1));
                preFilterRGB.put("upperCenter", getRGB(x, y-1));
                preFilterRGB.put("upperRight", getRGB(x+1, y-1));
                preFilterRGB.put("centerLeft", getRGB(x-1, y));
                preFilterRGB.put("center", getRGB(x, y));
                preFilterRGB.put("centerRight", getRGB(x+1, y));
                preFilterRGB.put("lowerLeft", getRGB(x+1, y+1));
                preFilterRGB.put("lowerCenter", getRGB(x, y+1));
                preFilterRGB.put("lowerRight", getRGB(x+1, y+1));

                Map<String, List<Integer>> xFilterRGB = preFilterRGB;
                Map<String, List<Integer>> yFilterRGB = preFilterRGB;

                // For each neighboring pixel to center, apply the x-filter
                for (String location : xFilterRGB.keySet()) {
                    int filterIndexX = 0;
                    int filterIndexY = 0;
                    
                    List<Integer> perLocationRGB = xFilterRGB.get(location);

                    // For each individual pixel in the current coordinates
                    for (int index = 0; index < 3; index++) {
                        perLocationRGB.set(index, perLocationRGB.get(index) * X_FILTER[filterIndexX][filterIndexY]);
                    }

                    if (filterIndexX < 2) {
                        filterIndexX++;
                    } else if (filterIndexY < 2) {
                        filterIndexX = 0;
                        filterIndexY++;
                    }
                }

                for (String location: xFilterRGB.keySet()) {
                    for (int color : xFilterRGB.get(location)) {
                        Gx += color;
                    }
                }

                Gx /= xFilterRGB.size() * 3;

                // For each neighboring pixel to center, apply the y-filter
                for (String location : yFilterRGB.keySet()) {
                    int filterIndexX = 0;
                    int filterIndexY = 0;
                    
                    List<Integer> perLocationRGB = yFilterRGB.get(location);

                    // For each individual pixel in the current coordinates
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

                G = (int) Math.sqrt(Math.pow(Gx, 2) + Math.pow(Gy, 2));

                setRGB(G, x, y);

                preFilterRGB.clear();
            }
        }
    }

    public static void main(String[] args) {
        Sobel sobel = new Sobel();

        sobel.loadImage("/Users/ben/Documents/ide-workspace/Java/Algorithms/221-sp23-hw01/res/source.jpg");

        sobel.applyFilter();

        sobel.saveImage("/Users/ben/Documents/ide-workspace/Java/Algorithms/221-sp23-hw01/res/output.jpg");
    }
}
