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
	
	/**
	 * returns Browser Name on which test would run.
	 */
	public String getBrowserName() {
		return this.browserName;
	}
	
	/**
	 * returns number of tests to be run in parallel.
	 */
	public int getCountOfNumberOfTests() {
		return this.numberOfTests;
	}
	
	/**
	 * if "true" then tests will run in parallel on the assigned browser.
	 */
	public boolean runTestsInParallel() {
		return this.runTestCasesInParallel;
	}
	
	/**
	 * if "true" then Browser will run tests in headless mode.
	 */
	public boolean headlessBrowser() {
		return this.headless;
	}

}
