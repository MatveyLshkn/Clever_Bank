package util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class PropertiesUtil {
    private static final ClassLoader CLASS_LOADER = PropertiesUtil.class.getClassLoader();
    private static final Properties PROPERTIES = new Properties();
    private static final Properties YAML_PROPERTIES = new Properties();

    static {
        loadProperties();
        loadYamlProperties();
    }

    /**
     * Loads properties from file application.properties
     */
    @SneakyThrows
    private static void loadProperties() {
        try (InputStream inputStream = CLASS_LOADER.getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        }
    }

    /**
     * Loads properties from file config.yaml
     */
    @SneakyThrows
    private static void loadYamlProperties() {
        try (InputStream yamlInputStream = CLASS_LOADER.getResourceAsStream("config.yml")) {
            YAML_PROPERTIES.load(yamlInputStream);
        }
    }

    /**
     * Gets property from file application.properties
     *
     * @param key a string key to a property
     * @return value of property
     */
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Gets property from file config.yaml
     *
     * @param key a string key to a property
     * @return value of property
     */
    public static String getYaml(String key) {
        return YAML_PROPERTIES.getProperty(key);
    }
}
