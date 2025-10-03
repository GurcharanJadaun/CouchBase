package deviceConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import FileManager.JsonFileManager;

public class DeviceManager {
	List<BrowserConfig> browsers;
	String operatingSystem, deviceConfigFileName;
	boolean runTestsOnBrowsersInParallel, isConfigValid;

	public DeviceManager(String deviceConfigFileName) {
		this.deviceConfigFileName = deviceConfigFileName;
		isConfigValid = true;
		browsers = new ArrayList<BrowserConfig>();
	}
	
	/**
	 * if true runs the configured browsers in parallel for testing on the device.
	 */
	public boolean runTestsOnBrowsersInParallel() {
		return this.runTestsOnBrowsersInParallel;
	}
	
	/**
	 * returns false if there was an issue while reading the device config file.
	 */
	public boolean isConfigValid() {
		return this.isConfigValid;
	}
	
	/**
	 * returns List<BrowserConfig>. The list has the browser configuration details.
	 */
	public List<BrowserConfig> getBrowserList() {
		return this.browsers;
	}
	
	/**
	 * returns JsonNode which contains one of the device configuration.
	 */
	private JsonNode getTestRunnerConfig(String configName) {
		JsonNode shortListedConfig = null;
		try {
			
			JsonFileManager config = new JsonFileManager();
			Iterator<JsonNode> listOfConfigs = config.getItemsFromJson(deviceConfigFileName);
			
			while(listOfConfigs.hasNext()) {
				JsonNode item = listOfConfigs.next();
				System.out.println("Checked config : "+item.get("ConfigName").asText());
				if(item.get("ConfigName").asText().equals(configName)){
					
					shortListedConfig = item.get("RunConfig");
				}
			}
			
					
		} catch (Exception ex) {
			ex.printStackTrace();
			isConfigValid = false;
		}
		
		return shortListedConfig;

	}
	
	/**
	 * Assigns Browser Details to the list of BrowserConfig from the JSON Array filtered by "ConfigName" parameter.
	 * 
	 * @param "configName" to be used for testing.
	 */
	public void getBrowserDetailsFromJson(String configName) {
		
		JsonNode shortListedConfig = this.getTestRunnerConfig(configName);
		if(shortListedConfig != null) {
			if (shortListedConfig.hasNonNull("TargetOperatingSystem")) {
		        this.operatingSystem = shortListedConfig.get("TargetOperatingSystem").asText();
		    }
			
			if (shortListedConfig.hasNonNull("ParallelBrowserExecution")) {
				this.runTestsOnBrowsersInParallel = shortListedConfig.get("ParallelBrowserExecution").asBoolean();
			}else {
				this.runTestsOnBrowsersInParallel = false;
			}
			
			shortListedConfig.get("TargetBrowsers").elements().forEachRemaining(item -> {
					browsers.add(new BrowserConfig(item));
				});
			
			
		}else {
			System.out.println("Config Name '" + configName+"' not available in DeviceConfig.JSON");
		}
		
	}
}
