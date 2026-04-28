package config;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigManager {

	public static Properties props;

	static {
		try {
			FileInputStream fin = new FileInputStream("./src/test/resources/config/config.properties");
			props = new Properties();
			props.load(fin);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getData(String key) {
		return props.getProperty(key);
	}
	
}