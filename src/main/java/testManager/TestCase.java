package testManager;

import java.util.ArrayList;
import java.util.List;

public class TestCase{
	String testCaseId,reason;
	List<TestStep> steps = new ArrayList<TestStep>();
	TestStatus result;
	
	public TestCase(){
		result = TestStatus.PENDING;
		reason = "";
	}
	
	public void insertTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}
	
	public void addSteps(TestStep step) {
		steps.add(step);
	}
	
	public List<TestStep> getSteps() {
		return steps;
	}
	
	public void setTestCaseResult(TestStatus result) {
		this.result = result;
	}
	public void setTestCaseResult(TestStatus result, String reason) {
		this.result = result;
		this.reason += reason;
	}
	
	public String getTestCaseReason() {
		return this.reason;
	}
	
	public TestStatus getTestCaseResult() {
		return this.result;
	}
	
	public String getTestCaseId( ) {
		return this.testCaseId;
	}
	

}
