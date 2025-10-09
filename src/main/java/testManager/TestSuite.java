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
	boolean isHookTestReliable;

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
	    this.isHookTestReliable = false;

	}

	// deep copy values of test suite
	public TestSuite(TestSuite suite) {
		this.suiteName = suite.suiteName;
		this.isTestSuiteValid = suite.isTestSuiteValid;
	    this.isHookTestReliable = suite.isHookTestReliable;

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
	 * 
	 * @param "testCase" add test case to the test suite.
	 */
	public void addTestCase(TestCase testCase) {
		testSuite.add(testCase);
	}

	/**
	 * adds list of test cases to the test suite.
	 * 
	 * @param "testCases" add list of test cases to the test suite.
	 */
	public void addTestCases(List<TestCase> testCases) {
		testSuite.addAll(testCases);
	}

	/**
	 * returns List<TestCase> from the test suite. These are the list of test cases
	 * for each test suite.
	 */
	public List<TestCase> getTestCases() {
		return this.testSuite;
	}

	/**
	 * returns List<TestCase> from the test suite. These are the list of test cases
	 * which should be executed before all test cases in test suite.
	 */
	public List<TestCase> getBeforeAllTests() {
		return this.beforeAllTests;
	}

	/**
	 * returns List<TestCase> from the test suite. These are the list of test cases
	 * which should be executed after all test cases in test suite.
	 */
	public List<TestCase> getAfterAllTests() {
		return this.afterAllTests;
	}

	/**
	 * returns List<TestCase> from the test suite. These are the list of test cases
	 * which should be executed before each test case in test suite.
	 */
	public List<TestCase> getBeforeEachTest() {
		return this.beforeEachTest;
	}

	/**
	 * returns List<TestCase> from the test suite. These are the list of test cases
	 * which should be executed after each test case in test suite.
	 */
	public List<TestCase> getAfterEachTest() {
		return this.afterEachTest;
	}

	/**
	 * sets name for the test suite.
	 * 
	 * @param "suiteName" adds name for the test suite.
	 */
	public void setSuitName(String suiteName) {
		this.suiteName = suiteName;
	}

	/**
	 * gets name of Test Suite.
	 */
	public String getSuiteName() {
		return this.suiteName;
	}

	/**
	 * returns first occurrence of Optional<TestCase> from the test suite.
	 */
	public Optional<TestCase> getFirstOccurenceOfTestCaseById(String testCaseId) {
		Optional<TestCase> tc;

		tc = testSuite.stream().filter(testCase -> testCase.getTestCaseId().equalsIgnoreCase(testCaseId)).findFirst();

		return tc;
	}

	/**
	 * returns List of TestCases from the test suite.
	 */
	public List<TestCase> getTestCasesById(String testCaseId) {
		List<TestCase> tc = Collections.emptyList();

		tc = testSuite.stream().filter(testCase -> testCase.getTestCaseId().equalsIgnoreCase(testCaseId))
				.collect(Collectors.toList());

		return tc;
	}

	/**
	 * extracts "before all" test cases from the test suite.
	 */
	public void extractBeforeAllMethodFromTestSuite() {
		this.beforeAllTests = this.getTestCasesById("beforeAll");
		this.testSuite.removeAll(this.beforeAllTests);
	}

	/**
	 * extracts "before each" test cases from the test suite.
	 */
	public void extractBeforeEachMethodFromTestSuite() {
		this.beforeEachTest = this.getTestCasesById("beforeEach");
		this.testSuite.removeAll(beforeEachTest);
	}

	/**
	 * extracts "after all" test cases from the test suite.
	 */
	public void extractAfterAllMethodFromTestSuite() {
		this.afterAllTests = this.getTestCasesById("afterAll");
		this.testSuite.removeAll(this.afterAllTests);
	}

	/**
	 * extracts "after each" test cases from the test suite.
	 */
	public void extractAfterEachMethodFromTestSuite() {
		this.afterEachTest = this.getTestCasesById("afterEach");
		this.testSuite.removeAll(this.afterEachTest);
	}

	/**
	 * returns List of Test Cases from the test suite filtered by TestStatus
	 */
	public List<TestCase> getListOfTestCasesByStatus(TestStatus status) {
		List<TestCase> shortListedTests = Collections.emptyList();
		shortListedTests = this.testSuite.stream().filter(tc -> tc.getTestCaseResult() == status)
				.collect(Collectors.toList());
		return shortListedTests;
	}

	/**
	 * removes all Invalid Test Cases from the test suite.
	 */
	public List<TestCase> removeInvalidTestCasesFromSuite() {
		List<TestCase> removedTests = Collections.emptyList();
		removedTests = getListOfTestCasesByStatus(TestStatus.INVALID);
		this.testSuite.removeAll(removedTests);
		return removedTests;
	}

	/**
	 * returns true when all the test cases in test suite have status as passed.
	 */
	public boolean hasTestSuitePassed() {

		boolean status = true;
		status = getListOfTestCasesByStatus(TestStatus.PASSED).size() == this.testSuite.size() ? true : false;
		return status;
	}

	/**
	 * returns true if any of the hooks (beforeEach, afterEach, afterAll, beforeAll)
	 * is present in test suite.
	 */
	public boolean suiteContainsHooks() {
		boolean result = true;

		result = this.getTestCasesById("beforeEach").size() > 0 || this.getTestCasesById("afterEach").size() > 0
				|| this.getTestCasesById("afterAll").size() > 0 || this.getTestCasesById("beforeAll").size() > 0;

		return result;
	}

	/**
	 * adds test status for the test suite.
	 * 
	 * @param "status" set status to the test suite.
	 */
	public void setTestSuiteStatus(TestStatus status) {
		this.isTestSuiteValid = status;
	}

	/**
	 * returns status of the test suite.
	 */
	public TestStatus getTestSuiteStatus() {
		return this.isTestSuiteValid;
	}

	/**
	 * returns true if status is not INVALID for test suite.
	 */
	public boolean isTestSuiteValid() {
		return !(this.isTestSuiteValid == TestStatus.INVALID);
	}

	public void setHookTestReliablity() {
		isHookTestReliable = true;
		for (TestCase tc : this.beforeEachTest) {
			if (!tc.hasTestCasePassed()) {
				isHookTestReliable = false;
				break;
			}
		}

		for (TestCase tc : this.afterEachTest) {
			if (!tc.hasTestCasePassed()) {
				isHookTestReliable = false;
				break;
			}
		}

	}

	public boolean isHookTestReliable() {
		return this.isHookTestReliable;
	}
	
	public void resetHookTestStatus() {
		this.beforeEachTest.forEach(
				testCase -> {
					testCase.setTestCaseResult(TestStatus.PENDING);
				});
		this.afterEachTest.forEach(
				testCase -> {
					testCase.setTestCaseResult(TestStatus.PENDING);
				});
	}

}
