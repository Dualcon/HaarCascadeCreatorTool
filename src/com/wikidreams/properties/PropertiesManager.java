package com.wikidreams.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesManager {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PropertiesManager.class); 
	public static Map<String, String> properties = new HashMap<String, String>();


	public static void loadProperties(String propFileName) {
		if (PropertiesManager.properties.isEmpty()) {
			InputStream inputStream = null;
			try {
				inputStream = PropertiesManager.class.getClassLoader().getResourceAsStream(propFileName);			
				if (inputStream != null) {
					Properties prop = new Properties();
					prop.load(inputStream);
					// Read project properties file
					PropertiesManager.properties.clear();
					PropertiesManager.properties.put("OpenCVBin", prop.getProperty("OpenCVBin"));
					PropertiesManager.properties.put("WorkSpace", prop.getProperty("WorkSpace"));
					PropertiesManager.properties.put("CascadesFolder", prop.getProperty("CascadesFolder"));

					PropertiesManager.logger.info("Project properties loaded.");
				} else {
					PropertiesManager.logger.error("property file '" + propFileName + "' not found in the classpath");
				}
			} catch (IOException e) {
				PropertiesManager.logger.error("File not found.");
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						PropertiesManager.logger.error(e.getMessage());
					}
				}
			} 
		}
	}



	public static void displayProperties() {
		for (Map.Entry<String, String> pair : PropertiesManager.properties.entrySet()) {
			System.out.println("Key: " + pair.getKey() + " Value: " + pair.getValue());
		}
	}

}
