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
	public TestCase(TestCase tc){
		this.result = TestStatus.PENDING;
		this.reason = "";
		this.steps = new ArrayList<TestStep>();
		for (TestStep ts : tc.getSteps()) {
	        this.steps.add(new TestStep(ts));
	    }
		this.testCaseId = tc.testCaseId;
		
		
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
