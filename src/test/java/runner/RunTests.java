package runner;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import TestReports.TestReports;
import deviceConfiguration.DeviceManager;
import loader.TestSuiteLoader;
import testManager.RunTestSuite;
import testManager.TestCase;
import testManager.TestStatus;
import testManager.TestStep;
import testManager.TestSuite;
import utilities.ExecuteStep;

public class RunTests implements RunTestSuite {

	List<TestSuite >listOfTestSuites;
	ExtentReports report;
	ExtentTest caseNode, suiteNode;
	ExecuteStep ex ;
	String browserName;
//	boolean flag;

	RunTests() {
		report = new ExtentReports();
	}

	public static void main(String args[]) {
		Instant start = Instant.now();
		TestSuiteLoader loadTests= new TestSuiteLoader();
		
		
		loadTests.setupTest();
		
		
		DeviceManager device = new DeviceManager("DeviceConfig");
		device.getBrowserDetailsFromJson("MacParallelTestRunner");
		
		device.getBrowserList().forEach(browser -> {
			RunTests test = new RunTests();
			test.browserName = browser.getBrowserName();
			test.listOfTestSuites = new ArrayList<TestSuite>(loadTests.listOfTestSuites.size());
			
			for(TestSuite suite : loadTests.listOfTestSuites) {
				test.listOfTestSuites.add(new TestSuite(suite));
			}
			
			System.out.println("---------" + test.browserName + "----------");
			test.run();
			
			Optional<String> suffix = Optional.ofNullable(browser+" ");
			new TestReports().createTestReport(test.report , suffix);
	
		});
	
		
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);

		long seconds = timeElapsed.toSeconds();
		double minutes = timeElapsed.toMinutes();
		System.out.println("Time taken: " + seconds + " seconds (" + minutes + " minutes)");

		System.exit(0);
	}

	public void run() {

		this.listOfTestSuites.forEach(suite -> {
			System.out.println("---------" + suite.getSuiteName() +"----------");
			suiteNode = report.createTest(suite.getSuiteName());
			
			if(suite.isTestSuiteValid()) {
			this.extractHooks(suite);
			this.runTestSuite(suite);
			
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
//		boolean flag = true;
//
//	//	flag = this.runListOfTestCases(beforeAllTests.getTestCases());
//
//		if (flag) {
//			for (TestCase testCase : testSuite.getTestCases()) {
//				caseNode = suiteNode.createNode(testCase.getTestCaseId());
//				Optional<String> deviceConfig = Optional.ofNullable(browserName);
//				ex = new ExecuteStep(deviceConfig);
//				
//				if (flag) {
//					flag = this.runListOfTestCases(testSuite.getBeforeEachTest());
//
//				} else {
//					// code to skip beforeEachTestCases here
//					testSuite.getBeforeEachTest().forEach(tc -> {
//						this.skipTestCase(tc,
//								"<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
//					});
//				}
//
//				if (flag) {
//					runTestCase(testCase);
//				} else {
//					this.skipTestCase(testCase,
//							"<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
//				}
//
//				if (flag) {
//					flag = this.runListOfTestCases(testSuite.getAfterEachTest());
//
//				} else {
//					// skip afterEachTestCases here
//					testSuite.getAfterEachTest().forEach(tc -> {
//						this.skipTestCase(tc,
//								"<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
//					});
//				}
//				
//			//	this.cleanUp();
//			}
//		}
//
//		if (flag) {
//			flag = this.runListOfTestCases(testSuite.getAfterAllTests());
//		} else {
//			afterAllTests.getTestCases().forEach(tc -> {
//				this.skipTestCase(tc,
//						"<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
//			});
//		}
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
			ex.flush();
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
			stepNode.fail("Step : " + ex.reason , ex.screenshot);
		}
	}

	public boolean runListOfTestCases(List<TestCase> listOfTestCases) {
		boolean result = true;
		
		for(TestCase testCase : listOfTestCases) {
			this.runTestCase(testCase);
			result = testCase.getTestCaseResult().isPassed(); // returns true if test is passed
		}
		
		return result;
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
		testSuite.extractAfterAllMethodFromTestSuite();
		testSuite.extractAfterEachMethodFromTestSuite();
		testSuite.extractBeforeAllMethodFromTestSuite();
		testSuite.extractBeforeEachMethodFromTestSuite();

	}

	public void cleanUp() {
		ex.executeStep("closeSession");
	}

}
