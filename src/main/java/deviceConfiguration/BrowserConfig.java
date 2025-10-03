package deviceConfiguration;

import com.fasterxml.jackson.databind.JsonNode;

public class BrowserConfig {
	
	String browserName;
	int numberOfTests;
	boolean runTestCasesInParallel, headless;
	
	BrowserConfig(JsonNode browserConfig){
		this.browserName = browserConfig.get("BrowserName").asText();
		
		if (browserConfig.hasNonNull("NumberOfTests")) {
			this.numberOfTests = browserConfig.get("NumberOfTests").asInt();
		}else {
			this.numberOfTests = 1;
		}
		
		if (browserConfig.hasNonNull("ParallelTestExecution")) {
			this.runTestCasesInParallel = browserConfig.get("ParallelTestExecution").asBoolean();
		}else {
			this.runTestCasesInParallel = false;
		}
		
		if (browserConfig.hasNonNull("HeadlessBrowser")) {
			this.headless = browserConfig.get("HeadlessBrowser").asBoolean();
		}else {
			this.headless = true;
		}
	}
	
	public String getBrowserName() {
		return this.browserName;
	}
	
	public int getCountOfNumberOfTests() {
		return this.numberOfTests;
	}
	
	public boolean runTestsInParallel() {
		return this.runTestCasesInParallel;
	}
	
	public boolean headlessBrowser() {
		return this.headless;
	}

}
