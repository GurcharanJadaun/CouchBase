package testManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestSuite {
	
	String suiteName;
	List<TestCase> testSuite;
	boolean isTestSuiteValid;
	
	public TestSuite(List<TestCase> listOfTestCases) {
		this.testSuite = listOfTestCases;
		this.isTestSuiteValid = true;
		
	}
	public TestSuite() {
		this.testSuite = new ArrayList<TestCase>();
		this.isTestSuiteValid = true;
		
	}
	
	public TestSuite(TestSuite suite) {
		this.suiteName = suite.suiteName;
		this.testSuite = new ArrayList<>(suite.testSuite); 
		this.isTestSuiteValid = suite.isTestSuiteValid;
	}
	/**
     * adds one test case at a time to the test suite.
     * @param "testCase" add test case to the test suite.
     */
	public void addTestCase(TestCase testCase) {
		testSuite.add(testCase);
	}
	
	/**
     * adds list of test cases to the test suite.
     * @param "testCases" add list of test cases to the test suite.
     */
	public void addTestCases(List<TestCase> testCases) {
		testSuite.addAll(testCases);
	}
	
	/**
     * fetches list of test cases from the test suite.
     */
	public List<TestCase> getTestCases(){
		return testSuite;
	}
	
	/**
     * sets name for the test suite.
     * @param "suiteName" adds name for the test suite.
     */
	public void setSuitName(String suiteName) {
		this.suiteName = suiteName;
	}
	
	public String getSuiteName() {
		return this.suiteName;
	}
	
	public Optional<TestCase> getFirstOccurenceOfTestCaseById(String testCaseId) {
		Optional<TestCase> tc;
		
		tc = testSuite.stream().filter(testCase -> testCase.getTestCaseId().equalsIgnoreCase(testCaseId)).findFirst();
		
		return tc;
	}
	
	public List<TestCase> getTestCasesById(String testCaseId) {
		List<TestCase> tc = Collections.emptyList();
		
		tc = testSuite.stream().filter(testCase -> testCase.getTestCaseId().equalsIgnoreCase(testCaseId)).collect(Collectors.toList());
		
		return tc;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public List<TestCase> getBeforeAllMethodFromTestSuite() {
		List<TestCase> tc;
		tc = this.getTestCasesById("beforeAll");
		this.testSuite.removeAll(tc);
		return tc;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public List<TestCase> getBeforeEachMethodFromTestSuite() {
		List<TestCase> tc;
		tc = this.getTestCasesById("beforeEach");
		this.testSuite.removeAll(tc);
		return tc;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public List<TestCase> getAfterAllMethodFromTestSuite() {
		List<TestCase> tc;
		tc = this.getTestCasesById("afterAll");
		this.testSuite.removeAll(tc);
		return tc;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public List<TestCase> getAfterEachMethodFromTestSuite() {
		List<TestCase> tc;
		tc = this.getTestCasesById("afterEach");
		this.testSuite.removeAll(tc);
		return tc;
	}
	
	public List<TestCase> getListOfTestCasesByStatus(TestStatus status){
		List<TestCase> shortListedTests = Collections.emptyList();
		shortListedTests = this.testSuite.stream().filter(tc -> tc.getTestCaseResult() == status).collect(Collectors.toList());
		return shortListedTests;
	}
	
	public List<TestCase> removeInvalidTestCasesFromSuite(){
		List<TestCase> removedTests = Collections.emptyList();
		removedTests = getListOfTestCasesByStatus(TestStatus.INVALID);
		this.testSuite.removeAll(removedTests);
		return removedTests;
	}
	
	public boolean hasTestSuitePassed() {
		
		boolean status = true;
		status = getListOfTestCasesByStatus(TestStatus.PASSED).size() == this.testSuite.size() ? true : false;
		return status;
	}
	
	public boolean suiteContainsHooks() {
		boolean result = true;
		
		result = this.getTestCasesById("beforeEach").size() > 0 || 
				 this.getTestCasesById("afterEach").size() > 0 ||
				 this.getTestCasesById("afterAll").size() > 0 ||
				 this.getTestCasesById("beforeAll").size() > 0;
				 
		return result;
	}
	
	public void setTestSuiteStatus(boolean status) {
		this.isTestSuiteValid = status;
	}
	
	public boolean isTestSuiteValid() {
		return this.isTestSuiteValid;
	}
	
	
}
