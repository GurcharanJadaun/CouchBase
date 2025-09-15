package loader;

import java.io.File;

import dataExtractor.XlsxDataExtractor;
import utilities.CleanupManager;

public class TestSuiteLoader extends XlsxDataExtractor {
	
	String locatorDir, keywordDir, pathSep;
	
	protected TestSuiteLoader(){
		this.pathSep = File.separator.toString();
		this.locatorDir = "src"+this.pathSep+"main"+this.pathSep+"resources"+this.pathSep+"CompilerDictionary"+this.pathSep+"LocatorDictionary";
		this.keywordDir = "src"+this.pathSep+"main"+this.pathSep+"resources"+this.pathSep+"CompilerDictionary"+this.pathSep+"KeywordDictionary";
	}
	
	
	public void setupTest() {
		CleanupManager clean = new CleanupManager();
		
		clean.flush();
		this.loadLocatorMap(locatorDir);
		this.loadFunctionNames(keywordDir);
		this.generateTestSuite();
		this.generateCompilationReport(listOfTestSuites);
	}
}
