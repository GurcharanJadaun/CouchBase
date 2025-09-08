package utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import testManager.TestStatus;

public class ExecuteStep {
	KeywordDictionary keyword;
	Class<?> keywordDictionaryClass;
	public TestStatus result;
	public String reason;

	public ExecuteStep() {
		keyword = new KeywordDictionary();
		keywordDictionaryClass = keyword.getClass();
		result = TestStatus.PASSED;
		reason = "";
	}

	public void executeStep(String methodName, String locator, String testData) {
		Method method;
		try {
			method = keywordDictionaryClass.getMethod(methodName, String.class, String.class);
			try {
				method.invoke(keyword, locator, testData);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// add logs here for failing to execute located method from Keyword class.
				this.setResultForException(e);
			} catch (InvocationTargetException e) {
				// add logs here for exception to execute the
				this.setResultForException(e);
			} catch (Exception e) {
				// add logs here for exception to execute the
				this.setResultForException(e);
			}
			//Thread.sleep(250);
		} catch ( Exception e) {
			// add logs here for failing to locate method in Keyword Class.
			this.setResultForException(e);
		}
	}

	/**
	 * Execute functions with one Parameter. Expects user to determine parameter
	 * passed is correct
	 * 
	 * @param "oneParameter" can either be locator or be test data depending on the
	 *                       function
	 */
	public void executeStep(String methodName, String oneParameter) {
		Method method;
		try {
			method = keywordDictionaryClass.getMethod(methodName, String.class);
			try {
				method.invoke(keyword, oneParameter);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// add logs here for failing to execute located method from Keyword class.
				this.setResultForException(e);
			} catch (InvocationTargetException e) {
				// add logs here for exception to execute the
				this.setResultForException(e);
			}
			//Thread.sleep(250);
		} catch (NoSuchMethodException | SecurityException e) {
			// add logs here for failing to locate method in Keyword Class.
			this.setResultForException(e);
		} catch (Exception e) {
			// add logs here for exception to execute the
			this.setResultForException(e);
		}
	}

	public void executeStep(String methodName) {
		Method method;
		try {
			method = keywordDictionaryClass.getMethod(methodName);
			try {
				method.invoke(keyword);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// add logs here for failing to execute located method from Keyword class.
				this.setResultForException(e);
			} catch (InvocationTargetException e) {
				// add logs here for exception to execute the
				this.setResultForException(e);
			}
			//Thread.sleep(250);
		} catch (NoSuchMethodException | SecurityException e) {
			// add logs here for failing to locate method in Keyword Class.
			this.setResultForException(e);
		} catch (Exception e) {
			// add logs here for exception to execute the
			this.setResultForException(e);
		}
	}

	void setResultForException(Exception ex) {
		if(ex.getCause()==null) {
		this.reason = ex.toString();	
		this.result = TestStatus.STOP_EXECUTION;
		}
		else{
		this.reason = ex.getCause().toString();
		if (reason.contains("SoftAssert")) {
			this.result = TestStatus.FAILED;
		} else {
			result = TestStatus.STOP_EXECUTION;
		}}
	}
}
