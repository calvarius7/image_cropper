package com.limmoor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Read image_cropper.properties
 *
 * @author limmoor
 * @since 27.08.2020
 */
public class PropertiesReader {

    public Properties readCroppingProperties() {
        Properties properties = new Properties();
        String propFileName = "image_cropper.properties";
        try (InputStream inputStream = new FileInputStream(findPropPath(propFileName).toFile())) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private Path findPropPath(String propFileName){
        Path path = null;
        try {
            path = Files.walk(Paths.get(ImageCropper.path)).filter(file -> file.getFileName().toString().equals(propFileName)).findFirst().get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

}
