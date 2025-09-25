package utilities;

import java.util.Arrays;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;

public class BrowserKeeper {
	Browser browser;
	Page page;
	Playwright playwright;

	@SuppressWarnings("unused")
	public void initiateBrowser(String browserName) {
		playwright = Playwright.create();
		BrowserContext context = null;
		if (browserName.equalsIgnoreCase("chrome")) {
			// ensures browser starts in maximized state
			browser = playwright.chromium().launch(
					new BrowserType.LaunchOptions().setHeadless(false).setArgs(Arrays.asList("--start-maximized")));
			context = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));

		} else if (browserName.equalsIgnoreCase("edge")) {
			browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("msedge")
					.setHeadless(false).setArgs(Arrays.asList("--start-maximized")));
			context = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));
		} else if (browserName.equalsIgnoreCase("firefox")) {
			browser = playwright.firefox().launch(
					new BrowserType.LaunchOptions().setHeadless(false).setArgs(Arrays.asList("--start-maximized")));
			context = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));

		} else if (browserName.equalsIgnoreCase("safari")) {
			browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
			context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1920,1080));
		}

		// it removes the fixed viewport
		// (seems like rendering issue is there with fixed 1280×720 option as Accept
		// cookies option isn't available)
		

		// Adds the tab on the browser
		page = context.newPage();
		this.movePageToTackleLazyLoad();
	}

	public void initiateHeadlessBrowser(String browserName) {
		playwright = Playwright.create();
		BrowserContext context = null;
		if (browserName.equalsIgnoreCase("chrome")) {
			// ensures browser works in headless state
			browser = playwright.chromium()
					.launch(new BrowserType.LaunchOptions().setHeadless(true)
							.setArgs(java.util.Arrays.asList("--headless=new",
									"--disable-blink-features=AutomationControlled", "--disable-gpu", "--no-sandbox",
									"--disable-dev-shm-usage")));
		} else if (browserName.equalsIgnoreCase("edge")) {
			browser = playwright.chromium()
					.launch(new BrowserType.LaunchOptions().setChannel("msedge").setHeadless(true).setArgs(
							java.util.Arrays.asList("--headless=new", "--no-sandbox", "--headless", "--disable-gpu")));
		} else if (browserName.equalsIgnoreCase("firefox")) {
			browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
		}

		context = browser.newContext(new Browser.NewContextOptions().setUserAgent(
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36")
				// .setViewportSize(1280, 720));
				.setViewportSize(null));
		// Adds the tab on the browser
		page = context.newPage();
		this.movePageToTackleLazyLoad();
	}

	public void openUrl(String url) {
		page.navigate(url);
	}

	public void movePageToTackleLazyLoad() {
		page.mouse().wheel(0, 300);
	}

	public void waitForPresenceOfElement(String locator) {
		page.waitForSelector(locator);
	}

	public boolean isElementAbsentInDom(String locator) {
		Locator ele = page.locator(locator);
		System.out.println("-->" + ele.count());
		return ele.count() == 0;
	}

	public void scrollIntoView(String locator) {
		page.locator(locator).scrollIntoViewIfNeeded();
	}

	public void clickWebElement(String locator) {
		Locator ele = page.locator(locator);
		ele.click();
	}

	public void enterTextInTextBox(String locator, String text) {
		Locator ele = page.locator(locator);
		// ele.fill(text);
		ele.pressSequentially(text);
	}

	public void selectValueFromDropDown(String locator, String selectByLabel) {
		Locator ele = page.locator(locator);
		SelectOption labelValue = new SelectOption();
		labelValue.setLabel(selectByLabel);
		ele.selectOption(labelValue);
	}

	public void selectIndexFromDropDown(String locator, int itemNumber) {
		Locator ele = page.locator(locator);
		SelectOption labelValue = new SelectOption();
		labelValue.setIndex(itemNumber);
		ele.selectOption(labelValue);
	}

	public boolean checkVisibilityOfElement(String locator) {
		boolean result = false;
		result = page.locator(locator).isVisible();
		return result;
	}

	public void waitForPageToRender() {
		try {
			page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(7000));
		} catch (Exception ex) {
			// must never fail this step due to exception
		}

	}

	public String getTextFromTextBox(String locator) {
		String text = "";
		Locator ele = page.locator(locator);
		text = ele.inputValue();
		try {
			text = text.trim();
		} catch (Exception ex) {
			text = "";
		}
		return text;
	}

	public String getTextFromElement(String locator) {
		String text = "";
		Locator ele = page.locator(locator);
		text = ele.textContent();
		return text;
	}

	public boolean isButtonEnabled(String locator) {
		Locator ele = page.locator(locator);
		boolean result = ele.isEnabled();
		return result;
	}

	public boolean isButtonDisabled(String locator) {
		Locator ele = page.locator(locator);
		boolean result = ele.isDisabled();
		return result;
	}

	public void pressKeyboardKey(String keyName) {
		page.keyboard().press(keyName);
	}

	public void closePage() {
		page.close();
	}

	public void closeBrowserSession() {
		if (browser != null && browser.isConnected()) {
			browser.close();
		}
		playwright.close();
	}

	public void closeBrowser() {
		browser.close();
	}

	public String getClassOfTheElement(String locator) {
		String classValue = page.locator(locator).getAttribute("class");
		return classValue;
	}

	public byte[] takeScreenshot() {
		byte[] screenShot = null;
		if (browser != null && browser.isConnected()) {
			screenShot = page.screenshot();
		}
		return screenShot;
	}

}
