package testManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestSuite {
	
	String suiteName;
	List<TestCase> testSuite, beforeAllTests, beforeEachTest, afterAllTests, afterEachTest;
	TestStatus isTestSuiteValid;
	
	public TestSuite(List<TestCase> listOfTestCases) {
		this.testSuite = listOfTestCases;
		this.isTestSuiteValid = TestStatus.PENDING;
		
	}
	public TestSuite() {
		this.testSuite = new ArrayList<TestCase>();
		this.beforeAllTests = new ArrayList<TestCase>();
		this.beforeEachTest = new ArrayList<TestCase>();
		this.afterAllTests = new ArrayList<TestCase>();
		this.afterEachTest = new ArrayList<TestCase>();
		
		this.isTestSuiteValid = TestStatus.PENDING;
		
	}
	
	public TestSuite(TestSuite suite) {
		this.suiteName = suite.suiteName;
		this.isTestSuiteValid = suite.isTestSuiteValid;
		
		this.testSuite = new ArrayList<TestCase>();
		this.beforeAllTests = new ArrayList<TestCase>();
		this.beforeEachTest = new ArrayList<TestCase>();
		this.afterAllTests = new ArrayList<TestCase>();
		this.afterEachTest = new ArrayList<TestCase>();
		
		for (TestCase tc : suite.testSuite) {
	        this.testSuite.add(new TestCase(tc));
	    }
		for (TestCase tc : suite.beforeAllTests) {
	        this.beforeAllTests.add(new TestCase(tc));
	    }
		for (TestCase tc : suite.beforeEachTest) {
	        this.beforeEachTest.add(new TestCase(tc));
	    }
		for (TestCase tc : suite.afterAllTests) {
	        this.afterAllTests.add(new TestCase(tc));
	    }
		for (TestCase tc : suite.afterEachTest) {
	        this.afterEachTest.add(new TestCase(tc));
	    }
		
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
		return this.testSuite;
	}
	
	public List<TestCase> getBeforeAllTests(){
		return this.beforeAllTests;
	}
	
	public List<TestCase> getAfterAllTests(){
		return this.afterAllTests;
	}
	
	public List<TestCase> getBeforeEachTest(){
		return this.beforeEachTest;
	}
	
	public List<TestCase> getAfterEachTest(){
		return this.afterEachTest;
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
	public void extractBeforeAllMethodFromTestSuite() {
		this.beforeAllTests = this.getTestCasesById("beforeAll");
		this.testSuite.removeAll(this.beforeAllTests);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void extractBeforeEachMethodFromTestSuite() {
		this.beforeEachTest = this.getTestCasesById("beforeEach");
		this.testSuite.removeAll(beforeEachTest);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void extractAfterAllMethodFromTestSuite() {
		this.afterAllTests = this.getTestCasesById("afterAll");
		this.testSuite.removeAll(this.afterAllTests);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void extractAfterEachMethodFromTestSuite() {
		this.afterEachTest = this.getTestCasesById("afterEach");
		this.testSuite.removeAll(this.afterEachTest);
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
				 this.getTestCasesById("afterEach").size()  > 0 ||
				 this.getTestCasesById("afterAll").size()   > 0 ||
				 this.getTestCasesById("beforeAll").size()  > 0;
				 
		return result;
	}
	
	public void setTestSuiteStatus(TestStatus status) {
		this.isTestSuiteValid = status;
	}
	
	public TestStatus getTestSuiteStatus() {
		return this.isTestSuiteValid;
	}
	
	public boolean isTestSuiteValid() {
		return !(this.isTestSuiteValid == TestStatus.INVALID);
	}
		
		
}
