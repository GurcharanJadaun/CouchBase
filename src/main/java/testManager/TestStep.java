package testManager;

import java.util.Optional;

public class TestStep {

	String action,locator,testData,reason,stepDescription;
	TestStatus result;
	int stepNumber;
	
	public TestStep(){
		reason = "";
		stepDescription = "";
		this.result = TestStatus.PENDING;
	}
	public TestStep(TestStep ts) {
		this.reason = "";
		this.result = TestStatus.PENDING;
		this.action = ts.action;
		this.locator = ts.locator;
		this.testData = ts.testData;
		this.stepNumber = ts.stepNumber;
		this.stepDescription = ts.stepDescription;
	}
	
	public void insertAction(String action) {
		 this.action = action;
	 }
	
	public void insertLocator(Optional<String> locator) {
		 this.locator = locator.orElse(null);
	 }
	
	public void insertTestData(Optional<String> testData) {
		 this.testData = testData.orElse(null);
	 }
	
	public void setStepDescription(Optional<String> description) {
		String data = description.orElse(this.action + "\t" + this.locator + "\t" + this.testData);
				//orElse(this.action + "\t" + this.locator + "\t" + this.testData);
		this.stepDescription = data;
	}
	
	public void updateStepDescription(String replace, String replaceWith) {
		this.stepDescription = this.stepDescription.replace(replace, replaceWith);
	}
	
	public void setResult(TestStatus result) {
		 this.result = result;
	 }
	
	public void setResult(TestStatus result, String reason) {
		 this.result = result;
		 this.reason += reason;
	 }
	
	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}
	 
	public String getStepDescription() {
		return this.stepDescription;
	}
	
	public String getAction() {
		return action;
	}
	public String getLocator() {
		return locator;
	}
	public String getTestData() {
		return testData;
	}
	public TestStatus getResult() {
		return result;
	}
	public String getTestStepReason() {
		return this.reason;
	}
	
}
