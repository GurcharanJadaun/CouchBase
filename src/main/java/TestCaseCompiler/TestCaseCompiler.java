package TestCaseCompiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import TestReports.TestReports;
import testManager.TestCase;
import testManager.TestStatus;
import testManager.TestStep;
import testManager.TestSuite;

public abstract class TestCaseCompiler {
	
	public ArrayList<String> functionNames;
	public HashMap<String,String> locators;
	
	public void generateCompilationReport(TestSuite suite) {
		
		TestReports reports = new TestReports(suite);
		reports.createCompilerReport();
		
		List<TestCase> shortListed = suite.removeInvalidTestCasesFromSuite();
		boolean status = new TestSuite(shortListed).suiteContainsHooks();
		suite.setTestSuiteStatus(!status);
		
		this.createReport(suite.getSuiteName(),shortListed);
		
	}
	
	public void generateCompilationReport(List <TestSuite> listOfSuites) {
		
		TestReports reports = new TestReports(listOfSuites);
		reports.createCompilerReport();
		
		for(TestSuite suite : listOfSuites) {
			List<TestCase> shortListed = Collections.emptyList();
				shortListed = suite.removeInvalidTestCasesFromSuite();
				boolean status = new TestSuite(shortListed).suiteContainsHooks();
				suite.setTestSuiteStatus(!status);
				this.createReport(suite.getSuiteName(),shortListed);
			
		}
		
	}
	
	void createReport(String suiteName,List<TestCase> shortListed) {
		System.out.println("--Test Compilation Completed For Suite " + suiteName + "--");
		int numberOfInvalidTests = shortListed.size(),index = 0;
		System.out.println("Number Of Invalid Test Cases : " + numberOfInvalidTests);
		for(TestCase tc : shortListed) {
			index++;
			System.out.println(index + " : " + tc.getTestCaseId() + "\t" + tc.getTestCaseResult() + "\n" + tc.getTestCaseReason());
		}
	}

	protected void compileTestCases(List<TestCase> testCaseList) {
		
		for(TestCase tc : testCaseList) {
			List<TestStep> listOfSteps = tc.getSteps();
			String failReason = "";
			boolean isTestCaseValid = true;
			for(TestStep step : listOfSteps) {
				String action = step.getAction();
				String locator = step.getLocator();
				String result ="";
				
			 if(!functionNames.contains(action)) {
				 	result = result+(">> Invalid Action Name : "+step.getAction()+"\n");
					step.setResult(TestStatus.INVALID,result);
					isTestCaseValid = false;
				} 	
				
			 if(locator!=null && !locators.containsKey(locator)) {
				 	result = result+(">> Invalid Locator Name : "+step.getLocator()+"\n");
					step.setResult(TestStatus.INVALID,result);
					isTestCaseValid = false;
				}
			 failReason += result;
			}
			if(isTestCaseValid) {
				assignLocators(listOfSteps);
			}else {
				tc.setTestCaseResult(TestStatus.INVALID, failReason);
			}
			
		}
	}
	public void assignLocators(List<TestStep> listOfSteps) {
		Iterator<TestStep> it = listOfSteps.iterator();
		while(it.hasNext()) {
			TestStep step = it.next();
			String loc = step.getLocator();
			String value = locators.get(loc);
			step.insertLocator(Optional.ofNullable(value));	
		}
	}
	public abstract void loadFunctionNames(String dir);
	public abstract void loadLocatorMap(String dir);
	
}
