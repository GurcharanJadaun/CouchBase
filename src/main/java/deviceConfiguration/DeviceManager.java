package deviceConfiguration;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import FileManager.JsonFileManager;

public class DeviceManager {
	List<String> browsers;
	String operatingSystem, deviceConfigFileName;
	boolean runTestsOnBrowsersInParallel, isConfigValid;

	public DeviceManager(String deviceConfigFileName) {
		this.deviceConfigFileName = deviceConfigFileName;
		isConfigValid = true;
		browsers = new ArrayList<String>();
	}
	
	public boolean runTestsInParallel() {
		return this.runTestsOnBrowsersInParallel;
	}

	public boolean isConfigValid() {
		return this.isConfigValid;
	}

	public List<String> getBrowserList() {
		return this.browsers;
	}

	public void setupDeviceFromJson() {

		try {
			JsonFileManager config = new JsonFileManager();
			JsonNode jsonItem = config.getItemFromJson(deviceConfigFileName).get("RunConfig");
			System.out.println(jsonItem.toString());
			
			this.operatingSystem = jsonItem.get("TargetOperatingSystem").asText();
			this.runTestsOnBrowsersInParallel = jsonItem.get("ParallelBrowserExecution").asBoolean();

			jsonItem.get("TargetBrowsers").elements().forEachRemaining(item -> {
				browsers.add(item.get("BrowserName").asText());
			});
			
		} catch (Exception ex) {
			ex.printStackTrace();
			isConfigValid = false;
		}

	}

}
