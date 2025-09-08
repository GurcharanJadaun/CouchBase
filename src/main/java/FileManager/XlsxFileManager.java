package FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import testManager.TestSuite;

public class XlsxFileManager {
	
	private Workbook excel;
	private FileInputStream fis;
	String pathSep,dir;
	public XlsxFileManager() {
		pathSep = "\\";
		this.dir = System.getProperty("user.dir") + "\\src\\main\\resources";
	}
	
	public Sheet getFirstExcelSheet(String folderName, String fileName) {
		Sheet sheet;
		
	    try {
			fis = new FileInputStream(dir + pathSep + folderName + pathSep + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			excel = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sheet = excel.getSheetAt(0);
		
		return sheet;
	}
	public List<String> getExcelFileNamesFrom(String folderName) {
		File folder = new File(dir + pathSep + folderName);
		File[] listOfFiles = folder.listFiles();
		List<String> fileNames = new ArrayList<String>();
		for(File file : listOfFiles) {
			String fileName =  file.getName();
			if(fileName.endsWith(".xlsx")) {
				fileNames.add(fileName);
				}
			}
		
		return fileNames;
	}
	
	public List<Sheet> getFirstExcelSheet(String folderName) {
		List<Sheet> listOfSheet= new ArrayList<Sheet>();
		
		File folder = new File(dir + pathSep + folderName);
		File[] listOfFiles = folder.listFiles();
		
		for(File file : listOfFiles) {
			String fileName =  file.getName();
			if(fileName.endsWith(".xlsx")) {
			try {
				fis = new FileInputStream(dir + pathSep + folderName + pathSep + fileName);
				try {
					
					excel = new XSSFWorkbook(fis);
					Sheet sheet = excel.getSheetAt(0);
					listOfSheet.add(sheet);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Directory looked into : "+dir + pathSep + folderName);
				}
				
				
				
			} catch (IOException e) {
				// Add logs here for File reading failure
				e.printStackTrace();
			}
		 }else {
			 // add logs for invalid fileType
			 System.out.println("Detected file with invalid Type : " + fileName);
		 }
		}
			
		return listOfSheet;
	}
	
	public List<Sheet> getAllExcelSheets(String folderName, String fileName) {
		List<Sheet> listOfSheets = new ArrayList<Sheet>();
		
	    try {
			fis = new FileInputStream(dir + pathSep + folderName + pathSep + fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			excel = new XSSFWorkbook(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int numberOfSheets = excel.getNumberOfSheets();
	
		while(numberOfSheets > 0) {
			numberOfSheets--;
			Sheet sheet = excel.getSheetAt(numberOfSheets);	
			listOfSheets.add(sheet);
		}
		
		return listOfSheets;
	}
	public HashMap<String,String> createDataDictionary(Sheet sheet,int keyCellIndex, int valueCellIndex, int skipRows){
		HashMap<String,String> dictionary= new HashMap<String,String>();
		int rowCount = 0;
		
		for(Row row : sheet) {
			if(skipRows > rowCount) {
				rowCount++;
				continue;
			}
			Cell keyCell = row.getCell(keyCellIndex-1);
			Cell valueCell = row.getCell(valueCellIndex-1);
			if(keyCell!=null && valueCell!=null) {
				dictionary.put(keyCell.getStringCellValue(), valueCell.getStringCellValue());
			}
		}
		return dictionary;
		
	}

}
