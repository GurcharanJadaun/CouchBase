package TestCaseCompiler;

import java.io.File;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unchecked")
public class DeviceConfigReader {
	private static Map<String, Object> configMap;

    // Load config.json into memory
    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            configMap = mapper.readValue(new File("config/deviceConfig.json"), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.json", e);
        }
    }

    // Method to get config value by key
    public static Object get(String key) {
        return configMap.get(key);
    }

    // Overloaded method to return String directly
    public static String getString(String key) {
        return String.valueOf(configMap.get(key));
    }

    // Overloaded method to return int
    public static int getInt(String key) {
        return Integer.parseInt(String.valueOf(configMap.get(key)));
    }

    // Overloaded method to return boolean
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(String.valueOf(configMap.get(key)));
    }
}
