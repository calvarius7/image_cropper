package com.limmoor;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

/**
 * Crop all .tiff images in user.dir -r.
 *
 * @author limmoor
 * @since 27.08.2020
 */
public class ImageCropper {

    public static String path = System.getProperty("user.dir");

    private static String croppedFolder = path + "/cropped/";
    private static int x;
    private static int y;
    private static int w;
    private static int h;

    public static void main(String[] args) {
        parseCroppingProperties();
        new File(croppedFolder).mkdirs();

        try {
            Files.walk(Paths.get(path)).parallel()
                    .filter(Files::isRegularFile)
                    .filter(ImageCropper::isNotAlreadyCropped)
                    .filter(ImageCropper::isTiff)
                    .forEach(ImageCropper::cropImage);
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    private static boolean isNotAlreadyCropped(Path file) {
        return !file.getParent().toString().endsWith("cropped");
    }

    private static boolean isTiff(Path file) {
        return file.toString().endsWith(".tiff") || file.toString().endsWith(".tif");
    }

    private static void parseCroppingProperties() {
        PropertiesReader reader = new PropertiesReader();
        Properties properties = reader.readCroppingProperties();
        ImageCropper.x = Integer.parseInt(properties.getProperty("x_coordinate"));
        ImageCropper.y = Integer.parseInt(properties.getProperty("y_coordinate"));
        ImageCropper.w = Integer.parseInt(properties.getProperty("width"));
        ImageCropper.h = Integer.parseInt(properties.getProperty("height"));
        System.out.println(String.format("Cropping with: x= %d, y= %d, width= %d px and height= %d px", x, y,w, h));
    }

    private static void cropImage(Path file) {
        try {
            BufferedImage originalImage = ImageIO.read(file.toFile());
            System.out.println("Original image Dimension: " + originalImage.getWidth() + "x" + originalImage.getHeight());

            BufferedImage croppedImage = originalImage.getSubimage(x, y, w, h);

            File outputFile = new File(croppedFolder + file.getFileName().toString());
            ImageIO.write(croppedImage, "tiff", outputFile);

            System.out.println("Image cropped successfully: " + outputFile.getPath());
        } catch (Exception e) {
            System.out.println("Couldn't crop " + file.toString() + ", wrong sizes / coordinates");
            System.out.println(e.getMessage());
        }
    }
}

