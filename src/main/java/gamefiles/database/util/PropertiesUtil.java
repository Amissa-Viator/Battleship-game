package gamefiles.database.util;

import java.io.IOException;
import java.util.Properties;
public class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();

    private PropertiesUtil(){}

    static {
        loadProperties();
    }
    private static void loadProperties() {
        try (var stream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String get(String name) {
        return PROPERTIES.getProperty(name);
    }

}
