package runner;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import TestReports.TestReports;
import loader.TestSuiteLoader;
import testManager.RunTestSuite;
import testManager.TestCase;
import testManager.TestStatus;
import testManager.TestStep;
import testManager.TestSuite;
import utilities.ExecuteStep;

public class RunTests extends TestSuiteLoader implements RunTestSuite {

	TestSuite beforeAllTests, beforeEachTest, afterAllTests, afterEachTest;
	ExtentReports report;
	ExtentTest caseNode, suiteNode;

	boolean flag;

	RunTests() {
		super();
		// TODO Auto-generated constructor stub
		report = new ExtentReports();
	}

	public static void main(String args[]) {
		Instant start = Instant.now();
		RunTests test = new RunTests();

		test.setupTest();
		test.run();
		new TestReports().createTestReport(test.report);

		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);

		long seconds = timeElapsed.toSeconds();
		double minutes = timeElapsed.toMinutes();
		System.out.println("Time taken: " + seconds + " seconds (" + minutes + " minutes)");
		System.exit(0);
	}

	public void run() {

		listOfTestSuites.forEach(suite -> {
			System.out.println("---------" + suite.getSuiteName() + "----------");
			suiteNode = report.createTest(suite.getSuiteName());
			
			if(suite.isTestSuiteValid()) {
			this.extractHooks(suite);
			this.runTestSuite(suite);
			this.cleanUp();
			}
			else {
				caseNode = suiteNode.createNode("Compilation Error In TestSuite");
				ExtentTest stepNode = caseNode.createNode("Test Suite Skipped Due Failures in Hook.");
				stepNode.skip("<<nPlease Look into the test Compilation report>>");
			}
			System.out.println("---------" + "Completed" + "----------");
		});

	}

	@Override
	public void runTestSuite(TestSuite testSuite) {
		flag = true;

		flag = this.runListOfTestCases(beforeAllTests.getTestCases());

		if (flag) {
			for (TestCase testCase : testSuite.getTestCases()) {
				caseNode = suiteNode.createNode(testCase.getTestCaseId());
				
				if (flag) {
					flag = this.runListOfTestCases(this.beforeEachTest.getTestCases());

				} else {
					// code to skip beforeEachTestCases here
					beforeEachTest.getTestCases().forEach(tc -> {
						this.skipTestCase(tc,
								"<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
					});
				}

				if (flag) {
					runTestCase(testCase);
				} else {
					this.skipTestCase(testCase,
							"<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
				}

				if (flag) {
					flag = this.runListOfTestCases(this.afterEachTest.getTestCases());

				} else {
					// skip afterEachTestCases here
					afterEachTest.getTestCases().forEach(tc -> {
						this.skipTestCase(tc,
								"<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
					});
				}
			}
		}

		if (flag) {
			flag = this.runListOfTestCases(afterAllTests.getTestCases());
		} else {
			afterAllTests.getTestCases().forEach(tc -> {
				this.skipTestCase(tc,
						"<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
			});
		}
	}

	@Override
	public void runTestCase(TestCase testCase) {
		Instant start = Instant.now();

		Iterator<TestStep> it = testCase.getSteps().iterator();
		while (it.hasNext()) {
			TestStep ts = it.next();
			if (testCase.getTestCaseResult().isFailed()) {
				this.skipStep(ts, ">> Skipped because of error above<< ");
			} else {
				runTestStep(ts);

				if (ts.getResult().isFailed()) {
					testCase.setTestCaseResult(ts.getResult().setStatusTo());
				}
			}
		}
		if (testCase.getTestCaseResult() == TestStatus.PENDING) {
			testCase.setTestCaseResult(TestStatus.PASSED);
		}
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);

		System.out.println("Executing : " + testCase.getTestCaseId() + "\t" + testCase.getTestCaseResult() + "\t"
				+ timeElapsed.toSeconds() + "\t" + testCase.getTestCaseReason());

	}

	@Override
	public void runTestStep(TestStep testStep) {

		ExecuteStep ex = new ExecuteStep();
		String action = testStep.getAction();
		String locator = testStep.getLocator();
		String testData = testStep.getTestData();

		ExtentTest stepNode = caseNode.createNode(action + " " + locator + " " + testData);

		if (locator == null && testData == null) {
			ex.executeStep(action);
		} else if (locator == null && testData != null) {
			ex.executeStep(action, testData);
		} else if (locator != null && testData == null) {
			ex.executeStep(action, locator);
		} else if (locator != null && testData != null) {
			ex.executeStep(action, locator, testData);
		} else {
			// Log error in logs here with step details like action, locator and testData
			testStep.setResult(TestStatus.INVALID, "Something missed by compiler\n<<-Didn't find a proper match->>\n");
		}
		 System.out.println("Executing : " + action + "\t" + locator + "\t" + testData
		 + "\t" + ex.result + "\n" + ex.reason);

		if (ex.result == TestStatus.PASSED) {
			testStep.setResult(TestStatus.PASSED);
			stepNode.pass( ex.reason);
		} else {
			testStep.setResult(ex.result, ex.reason);
			stepNode.fail("Step : " + ex.reason);
		}
	}

	public boolean runListOfTestCases(List<TestCase> listOfTestCases) {
		flag = true;
		listOfTestCases.forEach(testCase -> {
			this.runTestCase(testCase);
			flag = testCase.getTestCaseResult().isPassed(); // returns true if test is passed
		});
		return flag;
	}

	public void skipTestCase(TestCase testCase, String reason) {
		
		testCase.getSteps().forEach(step -> {
			this.skipStep(step, reason);
		});
	}

	public void skipStep(TestStep testStep, String reason) {
		ExtentTest stepNode = caseNode
				.createNode(testStep.getAction() + " " + testStep.getLocator() + " " + testStep.getTestData());
		stepNode.skip(reason);
	}

	public void extractHooks(TestSuite testSuite) {
		this.beforeAllTests = new TestSuite(testSuite.getBeforeAllMethodFromTestSuite());
		this.beforeEachTest = new TestSuite(testSuite.getBeforeEachMethodFromTestSuite());
		this.afterEachTest = new TestSuite(testSuite.getAfterEachMethodFromTestSuite());
		this.afterAllTests = new TestSuite(testSuite.getAfterAllMethodFromTestSuite());

	}

	public void cleanUp() {
		ExecuteStep ex = new ExecuteStep();
		ex.executeStep("closeSession");
	}

}
