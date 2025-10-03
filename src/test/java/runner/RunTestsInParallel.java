package runner;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.model.Report;

import TestReports.TestReports;
import deviceConfiguration.BrowserConfig;
import deviceConfiguration.DeviceManager;
import loader.TestSuiteLoader;
import testManager.TestCase;
import testManager.TestStatus;
import testManager.TestStep;
import testManager.TestSuite;
import utilities.ExecuteStep;

public class RunTestsInParallel {
	ExtentReports report;
	RunTestsInParallel() {
		report = new ExtentReports();
	}
	public static void main(String args[]) {
		Instant start = Instant.now();
		TestSuiteLoader loadTests= new TestSuiteLoader();
		
		
		loadTests.setupTest();
		
		
		DeviceManager device = new DeviceManager("DeviceConfig");
		device.getBrowserDetailsFromJson("TestRunner");
		
		RunTestsInParallel runner = new RunTestsInParallel();
		if(device.runTestsInParallel()) {
		runner.testBrowsersInParallel(device, loadTests.listOfTestSuites);}
		else {
		runner.testBrowsersSequentially(device, loadTests.listOfTestSuites);	
		}
		
			
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);

		long seconds = timeElapsed.toSeconds();
		double minutes = timeElapsed.toMinutes();
		System.out.println("Time taken: " + seconds + " seconds (" + minutes + " minutes)");

		System.exit(0);
	}
	
	public void testBrowsersSequentially(DeviceManager device, List<TestSuite> testSuites) {
		device.getBrowserList().forEach(browser -> {
			report = new ExtentReports(); // creates fresh report for each browser
			this.testSuiteSequential(testSuites, browser);
			Optional<String> suffix = Optional.ofNullable(browser.getBrowserName()+" ");
			new TestReports().createTestReport(this.report , suffix);
	
		});
	

	}
	
	public void testBrowsersInParallel(DeviceManager device, List<TestSuite> testSuites) {
		// do this later
		int numberOfThreads = device.getBrowserList().size();
		
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		
		for (BrowserConfig browser : device.getBrowserList()) {
			executor.submit(()-> testSuiteSequential(testSuites, browser));
		}
		
		executor.shutdown();
		try {
		    if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
		        executor.shutdownNow();
		    }
		} catch (InterruptedException e) {
		    executor.shutdownNow();
		    Thread.currentThread().interrupt();
		}
		
	}
	
	public void testSuiteSequential(List<TestSuite> testSuites, BrowserConfig browser) {
		for(TestSuite testSuite : testSuites) {
			TestSuite suite = (new TestSuite(testSuite));
		
		System.out.println("---------" + browser.getBrowserName() + "----------");
		ExtentTest suiteNode = this.report.createTest(suite.getSuiteName());
		
		this.extractHooks(suite);
		
		
		if(browser.runTestsInParallel()) {
		this.testCaseInParallel(suite,suiteNode,browser);}
		else {
		this.runTestSuite(suite, suiteNode, browser);	
		}
	}
	}
	
 	private void testCaseInParallel(TestSuite testSuite, ExtentTest suiteNode, BrowserConfig browserDetails) {
		int numberOfThreads = browserDetails.getCountOfNumberOfTests();

		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		
		for (TestCase testCase : testSuite.getTestCases()) {
			
			List<TestCase> combineTestCase = new ArrayList<TestCase>();
			
			combineTestCase.addAll(testSuite.getBeforeEachTest());
			combineTestCase.add(testCase);
			combineTestCase.addAll(testSuite.getAfterEachTest());
			
				
			final TestSuite minisuite = new TestSuite(); 
			minisuite.addTestCases(combineTestCase);
			this.extractHooks(minisuite);
			
			// create parallel runner here.
			executor.submit(() -> runTestSuite(minisuite, suiteNode, browserDetails));
	
		}

		executor.shutdown();
		try {
		    if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
		        executor.shutdownNow();
		    }
		} catch (InterruptedException e) {
		    executor.shutdownNow();
		    Thread.currentThread().interrupt();
		}

	}
	
	private void runTestSuite(TestSuite testSuite, ExtentTest suiteNode, BrowserConfig browserDetails) {
		
	//	flag = this.runListOfTestCases(beforeAllTests.getTestCases());
		boolean flag =true;
			
			for (TestCase testCase : testSuite.getTestCases()) {
			ExtentTest caseNode = suiteNode.createNode(testCase.getTestCaseId());
				Optional<BrowserConfig> deviceConfig = Optional.ofNullable(browserDetails);
				ExecuteStep ex = new ExecuteStep(deviceConfig);
				
				
				if (flag) {
					
					flag = this.runListOfTestCases(testSuite.getBeforeEachTest(), caseNode, ex);
					
				} else {
					// code to skip beforeEachTestCases here
					testSuite.getBeforeEachTest().forEach(tc -> {
						this.skipTestCase(tc, caseNode, "<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
					});
				}

				if (flag) {
					this.runTestCase(testCase, caseNode, ex);
				} else {
					this.skipTestCase(testCase, caseNode, "<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
				}

				if (flag) {
					flag = this.runListOfTestCases(testSuite.getAfterEachTest(), caseNode, ex);

				} else {
					// skip afterEachTestCases here
					testSuite.getAfterEachTest().forEach(tc -> {
						this.skipTestCase(tc, caseNode, "<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
					});
				}
				
				this.cleanUp(ex);
			}

//		if (flag) {
//			flag = this.runListOfTestCases(testSuite.getAfterAllTests());
//		} else {
//			afterAllTests.getTestCases().forEach(tc -> {
//				this.skipTestCase(tc,
//						"<< skipping tests due to Hook (beforeAll, afterAll, beforeEach, afterEach) failure >>");
//			});
//		}
	}
	
	private void cleanUp(ExecuteStep ex) {
		ex.executeStep("closeSession");
	}


	private void runTestCase(TestCase testCase, ExtentTest testCaseNode, ExecuteStep ex) {
		Instant start = Instant.now();

		Iterator<TestStep> it = testCase.getSteps().iterator();
		while (it.hasNext()) {
			TestStep ts = it.next();
			if (testCase.getTestCaseResult().isFailed()) {
				this.skipStep(ts,testCaseNode ,">> Skipped because of error above<< ");
			} else {
				runTestStep(ts, testCaseNode, ex);

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

	
	private void skipTestCase(TestCase testCase, ExtentTest testCaseNode, String reason) {
		testCase.getSteps().forEach(step -> {
			this.skipStep(step, testCaseNode, reason);
		});

	}

	private boolean runListOfTestCases(List<TestCase> listOfTestCases, ExtentTest testCaseNode, ExecuteStep ex) {
		boolean result = true;
		
		for(TestCase testCase : listOfTestCases) {
			this.runTestCase(testCase,testCaseNode,ex);
			result = testCase.getTestCaseResult().isPassed(); // returns true if test is passed
		}
		
		return result;
	}

	
	private void skipStep(TestStep testStep, ExtentTest testCaseNode ,String reason) {
		ExtentTest stepNode = testCaseNode
				.createNode(testStep.getAction() + " " + testStep.getLocator() + " " + testStep.getTestData());
		stepNode.skip(reason);
	}

	private void runTestStep(TestStep testStep, ExtentTest testCaseNode, ExecuteStep ex) {

		String action = testStep.getAction();
		String locator = testStep.getLocator();
		String testData = testStep.getTestData();

		ExtentTest stepNode = testCaseNode.createNode(action + " " + locator + " " + testData);

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
		System.out.println(
				"Executing : " + action + "\t" + locator + "\t" + testData + "\t" + ex.result + "\n" + ex.reason);

		if (ex.result == TestStatus.PASSED) {
			testStep.setResult(TestStatus.PASSED);
			stepNode.pass(ex.reason);
		} else {

			testStep.setResult(ex.result, ex.reason);
			stepNode.fail("Step : " + ex.reason, ex.screenshot);
		}
	}
	
	private void extractHooks(TestSuite testSuite) {
		testSuite.extractAfterAllMethodFromTestSuite();
		testSuite.extractAfterEachMethodFromTestSuite();
		testSuite.extractBeforeAllMethodFromTestSuite();
		testSuite.extractBeforeEachMethodFromTestSuite();

	}
}
