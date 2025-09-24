package dataExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import FileManager.XlsxFileManager;
import TestCaseCompiler.TestCaseCompiler;
import testManager.CreateTestSuite;
import testManager.TestCase;
import testManager.TestStep;
import testManager.TestSuite;

public class XlsxDataExtractor extends TestCaseCompiler implements CreateTestSuite {

	public List<TestSuite> listOfTestSuites;
	private Sheet sheet;
	String pathSep, dir;

	protected XlsxDataExtractor() {
		pathSep = File.separator.toString();
		listOfTestSuites = new ArrayList<TestSuite>();
		dir = "src" + pathSep + "main" + pathSep + "resources" + pathSep + "FeatureFiles";
	}

	public void generateTestSuite() {

		XlsxFileManager fileManager = new XlsxFileManager();
		List<String> fileNames = fileManager.getExcelFileNamesFrom(dir);

		for (String fileName : fileNames) {
			this.loopOverTestSuites(fileName);
		}

	}
	
	public void generateSuiteFromTestPlan() {
		XlsxFileManager fileManager = new XlsxFileManager();
		
		sheet = fileManager.getFirstExcelSheet("TestPlan.xlsx");
		
		HashMap<String, String> tmp = fileManager.createDataDictionary(sheet, 3, 4, 1);
			
		for(Map.Entry<String, String> item : tmp.entrySet()) {
			if(item.getValue().equalsIgnoreCase("y")) {
				this.loopOverTestSuites(item.getKey());
			}
		}
	}

	 void loopOverTestSuites(String suiteName) {
		try {
			TestSuite suite = loadTestCasesFromFile(suiteName);
			listOfTestSuites.add(suite);
		} catch (IOException e) {
			// Add logs here for File reading failure
			e.printStackTrace();
		}
	}
	
	@Override
	public TestSuite loadTestCasesFromFile(String fileName) throws IOException {
		TestSuite suite = new TestSuite();
		XlsxFileManager fileManager = new XlsxFileManager();
		suite.setSuitName(fileName);

		sheet = fileManager.getFirstExcelSheet(this.dir, fileName);
		List<TestCase> listOfTests = createListOfTestCases();

		this.compileTestCases(listOfTests);
		suite.addTestCases(listOfTests);

		return suite;
	}

	@Override
	public List<TestCase> createListOfTestCases() {
		String tmp = "";
		List<TestCase> listOfTestCases = new ArrayList<TestCase>();
		int stepNumber = 0, skipRow = 1, rowCount = 0;
		TestCase tc = null;

		for (Row row : sheet) {
			if (skipRow > rowCount) {
				rowCount++;
				continue;
			}
			TestStep ts = new TestStep();

			Cell testCaseIdCell = row.getCell(0);
			Cell actionCell = row.getCell(1);
			Cell locatorCell = row.getCell(2);
			Cell testDataCell = row.getCell(3);

			if (testCaseIdCell != null && testCaseIdCell.getStringCellValue().length() > 0) {

				if (tc != null) {
					listOfTestCases.add(tc);
				}

				tmp = testCaseIdCell.getStringCellValue();
				stepNumber = 1;
				tc = new TestCase();
				tc.insertTestCaseId(tmp);
			}
			if (actionCell != null && actionCell.getStringCellValue().length() > 0) {
				String action = actionCell.getStringCellValue();
				ts.insertAction(action);
			} else {
				continue;
			}
			Optional<String> locator = Optional.ofNullable(locatorCell).map(Cell::toString)
					.filter(s -> !s.trim().isEmpty());
			;
			Optional<String> testData = Optional.ofNullable(testDataCell).map(Cell::toString)
					.filter(s -> !s.trim().isEmpty());

			ts.insertLocator(locator);
			ts.insertTestData(testData);
			ts.setStepNumber(stepNumber);

			tc.addSteps(ts);
			stepNumber++;

		}
		listOfTestCases.add(tc);

		return listOfTestCases;

	}

	public void loadLocatorMap(String dir) {
		XlsxFileManager fileManager = new XlsxFileManager();
		this.locators = new HashMap<String, String>();

		List<Sheet> listOfSheets = fileManager.getFirstExcelSheetFromAllFiles(dir);
		Iterator<Sheet> it = listOfSheets.iterator();
		while (it.hasNext()) {
			Sheet sheet = it.next();
			HashMap<String, String> tmp = fileManager.createDataDictionary(sheet, 2, 3, 1);
			this.locators.putAll(tmp);
		}

		System.out.println("Extracted total number of Locators : " + this.locators.size());
	}

	@Override
	public void loadFunctionNames(String dir) {
		XlsxFileManager fileManager = new XlsxFileManager();
		this.functionNames = new ArrayList<String>();

		List<Sheet> listOfSheets = fileManager.getFirstExcelSheetFromAllFiles(dir);
		Iterator<Sheet> it = listOfSheets.iterator();
		while (it.hasNext()) {
			Sheet sheet = it.next();
			HashMap<String, String> tmp = fileManager.createDataDictionary(sheet, 3, 4, 1);
			this.functionNames.addAll(tmp.keySet());
		}

		System.out.println("Extracted total number of Functions : " + this.functionNames.size());

	}

}
