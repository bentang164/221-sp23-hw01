package edu.macalester.comp221.hw1;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Sobel {
    private static final int[][] X_FILTER = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    private static final int[][] Y_FILTER = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

    private BufferedImage source;
    private BufferedImage output;
    
    public Sobel() {
        source = output = null;
    }

    private void loadImage(String path) {
        try {
            source = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Exception reading specified source file");
            e.printStackTrace();
        }
    }

    private void saveImage(String path) {
        try {
            File outputFile = new File("/res/output.png");
            ImageIO.write(output, "png", outputFile);
        } catch (IOException e) {
            System.out.println("Exception saving specified output file");
            e.printStackTrace();
        }
    }

    private List<Integer> getRGB(BufferedImage input, int x, int y) {
        List<Integer> rgbArray = new ArrayList<>();
        Color imageRGB = new Color(input.getRGB(x, y));

        rgbArray.add(imageRGB.getRed());
        rgbArray.add(imageRGB.getGreen());
        rgbArray.add(imageRGB.getBlue());

        return rgbArray;
    }

    private void applyFilter() {

    }

    public static void main(String[] args) {
        Sobel sobel = new Sobel();

        sobel.loadImage("/res/source.jpg");
    }
}
