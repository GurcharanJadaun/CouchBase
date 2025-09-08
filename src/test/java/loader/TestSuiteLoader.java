package loader;

import dataExtractor.XlsxDataExtractor;

public class TestSuiteLoader extends XlsxDataExtractor {
	
	String locatorDir, keywordDir;
	
	protected TestSuiteLoader(){
		this.locatorDir = "CompilerDictionary\\LocatorDictionary";
		this.keywordDir = "CompilerDictionary\\KeywordDictionary";
	}
	
	
	public void setupTest() {
		this.loadLocatorMap(locatorDir);
		this.loadFunctionNames(keywordDir);
		this.generateTestSuite();
		this.generateCompilationReport(listOfTestSuites);
	}
}
