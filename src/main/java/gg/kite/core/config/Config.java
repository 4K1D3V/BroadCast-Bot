package gg.kite.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The Config class provides utility methods to load and retrieve
 * application configuration properties from a file named "config.properties"
 * located in the classpath. It ensures that the configuration is loaded
 * once when the class is initialized and allows retrieval of property values
 * by their keys.
 *
 * The class uses a static block to load the properties at runtime. If the
 * "config.properties" file is missing or an error occurs during loading,
 * the application will terminate with an error message.
 */
public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                System.err.println("Error: config.properties file not found in classpath.");
                System.exit(1);
            } else {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            System.err.println("Error while loading config.properties: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}