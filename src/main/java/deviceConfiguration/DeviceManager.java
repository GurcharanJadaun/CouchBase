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
	
	public boolean runTestsInParallel() {
		return this.runTestsOnBrowsersInParallel;
	}

	public boolean isConfigValid() {
		return this.isConfigValid;
	}

	public List<BrowserConfig> getBrowserList() {
		return this.browsers;
	}
	
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
