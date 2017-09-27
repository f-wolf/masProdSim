package de.felixwolf.masProdSim.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by felix on 22.03.17.
 *
 * Static class to retrieve the settings
 */

public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private static Properties properties = new Properties();
    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static String fileName = "settings.properties";

    static {
        try {
            InputStream resourceStream = loader.getResourceAsStream(fileName);
            properties.load(resourceStream);
            resourceStream.close();
        }
        catch (Exception e){
            LOGGER.error("Properties could not be read");
            e.printStackTrace();
            System.exit(1);
        }
    }


    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Methods to retrieve different property type. In case of a failure (wrong input or property not defined) the
     * default value is returned.
     */


    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getIntegerProperty(String key, int defaultValue){

        int value = defaultValue;

        try {
            String strValue = properties.getProperty(key);
            int tempValue = Integer.parseInt(strValue);
            value = tempValue;
        }catch (Exception e){
            LOGGER.warn("Retrieving integer value for key '" + key + "' failed. The default value is used.");
        }

        return value;
    }

    public static int[] getIntArrayProperty(String key, int[] defaultValue){

        int[] value = defaultValue;

        try {
            String strValue = properties.getProperty(key);
            String[] splittedString = strValue.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            int[] tempValue = new int[splittedString.length];
            for (int i = 0; i < splittedString.length; i++) {
                tempValue[i] = Integer.parseInt(splittedString[i]);
            }
            value = tempValue;

        }catch (Exception e){
            LOGGER.warn("Retrieving integer array value for key '" + key + "' failed. The default value is used.");
        }

        return value;
    }


    public static double getDoubleProperty(String key, double defaultValue){

        double value = defaultValue;

        try {
            String strValue = properties.getProperty(key);
            double tempValue = Double.parseDouble(strValue);
            value = tempValue;
        }catch (Exception e){
            LOGGER.warn("Retrieving double value for key '" + key + "' failed. The default value is used.");
        }

        return value;
    }
}
