package com.qaprosoft.zafira.tests.gui;

import com.qaprosoft.zafira.tests.util.Config;
import com.qaprosoft.zafira.tests.util.webdriver.SearchElementLocatorFactory;
import com.qaprosoft.zafira.tests.util.webdriver.UIElementDecorator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractUIObject
{

	protected static final Logger LOGGER = Logger.getLogger(AbstractPage.class);
	protected static final Long IMPLICITLY_TIMEOUT = 15L;
	protected int ADMIN_ID = Integer.valueOf(Config.get("admin1.id"));
	protected int PERFORMANCE_DASHBOARD_ID = Integer.valueOf(Config.get("dashboard.performance.id"));

	protected WebDriver driver;
	protected SearchContext context;
	protected WebElement rootElement;

	@FindBy(xpath = "//md-backdrop")
	private WebElement backdrop;

	@FindBy(xpath = "//md-tooltip")
	private WebElement tooltip;

	@FindBy(xpath = "//div[contains(@class, 'ajs-success')]")
	private WebElement successAlert;

	@FindBy(xpath = "//div[contains(@class, 'ajs-error')]")
	private WebElement errorAlert;

	@FindBy(xpath = "//div[contains(@class, 'ajs-warning')]")
	private WebElement warningAlert;

	protected AbstractUIObject(WebDriver driver)
	{
		this(driver, driver);
	}

	protected AbstractUIObject(WebDriver driver, SearchContext context)
	{
		this.driver = driver;
		this.context = context;
		ElementLocatorFactory elementLocatorFactory = new SearchElementLocatorFactory(context);
		PageFactory.initElements(new UIElementDecorator(driver, elementLocatorFactory), this);
	}

	public boolean isElementPresent(By by, long seconds)
	{
		return innerTimeoutOperation(() -> {
			Wait webDriverWait = new WebDriverWait(driver, seconds, 100L);
			webDriverWait.until(dr -> driver.findElement(by).isDisplayed());
			return webDriverWait;
		}, "Start to find element "  + by.toString(), "Finish to find element " + by.toString());
	}

	public boolean isElementPresent(WebElement webElement, By by, long seconds)
	{
		String element = by == null ? "" : by.toString();
		return innerTimeoutOperation(() -> {
			Wait webDriverWait = new WebDriverWait(driver, seconds, 100L);
			webDriverWait.until(dr -> webElement.findElement(by).isDisplayed());
			return webDriverWait;
		}, "Start to find element "  + element, "Finish to find element " + element);
	}

	public boolean isElementPresent(WebElement webElement, long seconds)
	{
		return waitUntilElementIsPresent(webElement, seconds);
	}

	public boolean isElementClickable(WebElement webElement, long seconds)
	{
		return waitUntilElementToBeClickable(webElement, seconds);
	}

	public boolean waitUntilElementIsPresent(By by, long seconds)
	{
		return innerTimeoutOperation(() -> {
			WebDriverWait webDriverWait = new WebDriverWait(driver, seconds, 0L);
			webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
			return webDriverWait;
		}, "Start to wait until element is present "  + by.toString(), "Finish to wait element " + by.toString());
	}

	public boolean waitUntilElementIsPresent(WebElement webElement, By by, long seconds)
	{
		return innerTimeoutOperation(() -> {
			Wait webDriverWait = new WebDriverWait(driver, seconds, 0L);
			webDriverWait.until(dr -> webElement.findElement(by).isDisplayed());
			return webDriverWait;
		}, "Start to wait until element is present "  + by.toString(), "Finish to wait element " + by.toString());
	}

	public boolean waitUntilElementIsPresent(WebElement webElement, long seconds)
	{
		return innerTimeoutOperation(() -> {
			WebDriverWait webDriverWait = new WebDriverWait(driver, seconds, 0L);
			webDriverWait.until(ExpectedConditions.visibilityOf(webElement));
			return webDriverWait;
		}, "Start to wait until element is present", "Finish to wait element");
	}

	public boolean waitUntilElementIsNotPresent(WebElement webElement, long seconds)
	{
		return innerTimeoutOperation(() -> {
			WebDriverWait webDriverWait = new WebDriverWait(driver, seconds, 0L);
			webDriverWait.until(ExpectedConditions.invisibilityOfAllElements(Arrays.asList(webElement)));
			return webDriverWait;
		}, "Start to wait until element is not present", "Finish to wait element");
	}

	public boolean waitUntilElementWithTextIsPresent(WebElement webElement, String text, long seconds)
	{
		return innerTimeoutOperation(() -> {
			WebDriverWait webDriverWait = new WebDriverWait(driver, seconds, 0L);
			webDriverWait.until(ExpectedConditions.textToBePresentInElement(webElement, text));
			return webDriverWait;
		}, "Start to wait until element is not present", "Finish to wait element");
	}

	public boolean waitUntilElementToBeClickable(WebElement webElement, long seconds)
	{
		return innerTimeoutOperation(() -> {
			WebDriverWait webDriverWait = new WebDriverWait(driver, seconds, 0L);
			webDriverWait.until(ExpectedConditions.elementToBeClickable(webElement));
			return webDriverWait;
		}, "Start to wait until element is clickable", "Finish to wait element");
	}

	public boolean waitUntilElementToBeClickableByBackdropMask(WebElement webElement, long seconds)
	{
		return waitUntilElementIsNotPresent(backdrop, seconds) && waitUntilElementToBeClickable(webElement, seconds);
	}

	public boolean waitUntilElementToBeClickableWithBackdropMask(WebElement webElement, long seconds)
	{
		return waitUntilElementIsPresent(getBackdrop(), 1) && waitUntilElementToBeClickable(webElement, seconds);
	}

	private boolean innerTimeoutOperation(Supplier<Wait> operationSupplier, String startMessage, String finishMessage)
	{
		boolean result = false;
		try
		{
			driver.manage().timeouts().implicitlyWait(0L, TimeUnit.SECONDS);
			LOGGER.info(startMessage);
			operationSupplier.get();
			result = true;
		} catch (Exception e)
		{
			result = false;
		} finally
		{
			LOGGER.info("Status " + Boolean.toString(result) + ": " + finishMessage);
			driver.manage().timeouts().implicitlyWait(IMPLICITLY_TIMEOUT, TimeUnit.SECONDS);
		}
		return result;
	}

	public void hoverOnElement(WebElement webElement)
	{
		Actions actions = new Actions(driver);
		actions.moveToElement(webElement).perform();
	}

	public String getTooltipText()
	{
		return tooltip.getText();
	}

	public String hoverAndGetTooltipText(WebElement webElement)
	{
		hoverOnElement(webElement);
		return getTooltipText();
	}

	public void clickOutside()
	{
		clickByCoordinates("1", "1");
		waitUntilElementIsNotPresent(getBackdrop(), 4);
	}

	public void clickByCoordinates(String x, String y)
	{
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript(String.format("$(document.elementFromPoint(%s, %s)).click();", x, y));
	}

	public void pause(double timeout)
	{
		try
		{
			Thread.sleep(new Double(timeout * 1000).intValue());
		} catch (InterruptedException e)
		{
			LOGGER.error(e.getMessage());
		}
	}

	public String getWebElementValue(WebElement webElement)
	{
		return webElement.getAttribute("value");
	}

	public boolean hasDisabledAttribute(WebElement webElement)
	{
		return ! StringUtils.isBlank(webElement.getAttribute("disabled")) && ! webElement.getAttribute("disabled").equals("disabled");
	}

	public boolean hasSelectedAttribute(WebElement webElement)
	{
		return ! StringUtils.isBlank(webElement.getAttribute("selected"));
	}

	public WebElement getBackdrop()
	{
		return backdrop;
	}

	public WebElement getTooltip()
	{
		return tooltip;
	}

	public WebElement getSuccessAlert()
	{
		return successAlert;
	}

	public WebElement getErrorAlert()
	{
		return errorAlert;
	}

	public WebElement getWarningAlert()
	{
		return warningAlert;
	}

	public WebElement getRootElement()
	{
		return rootElement;
	}

	public void setRootElement(WebElement rootElement)
	{
		this.rootElement = rootElement;
	}

	public boolean isElementPresent(long seconds)
	{
		return isElementPresent(getRootElement(), seconds);
	}

	public void clearAllInputs()
	{
		getRootElement().findElements(By.xpath(".//input[not(@type = 'checkbox') and not(@disabled)] | .//textarea")).forEach(input -> {
			while(! input.getAttribute("value").isEmpty())
			{
				input.click();
				input.sendKeys(Keys.BACK_SPACE);
			}
		});
	}

	public boolean isChecked(WebElement webElement)
	{
		return Arrays.asList(webElement.getAttribute("class").split(" ")).contains("md-checked");
	}

	public void check(WebElement webElement)
	{
		if(! isChecked(webElement))
			webElement.click();
	}

	public void uncheck(WebElement webElement)
	{
		if(isChecked(webElement))
			webElement.click();
	}

	public void switchToWindow()
	{
		Set<String> windowHandles = driver.getWindowHandles();
		windowHandles.remove(driver.getWindowHandle());
		if(windowHandles.size() != 0)
			driver.switchTo().window((String) windowHandles.toArray()[0]);
	}

	public void select(WebElement webElement, String value)
	{
		String id = webElement.getAttribute("aria-owns");
		webElement.click();
		driver.findElement(By.xpath("//div[@id = '" + id + "' and preceding-sibling::header]//md-option[.//*[contains(text(), '" + value + "') "
				+ "or contains(text(), '" + value + "')]]")).click();
	}

	public String getSelectedValue(WebElement webElement)
	{
		return webElement.findElement(By.xpath(".//md-select-value//div")).getText();
	}

	public String getCurrentNodeText(WebElement webElement)
	{
		String text = webElement.getText();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try
		{
			List<WebElement> childs = webElement.findElements(By.xpath("./*"));
			for (WebElement child : childs)
			{
				text = text.replaceFirst(child.getText(), "");
			}
		} finally
		{
			driver.manage().timeouts().implicitlyWait(IMPLICITLY_TIMEOUT, TimeUnit.SECONDS);
		}
		return text.trim();
	}

	public Alert getAlert()
	{
		return driver.switchTo().alert();
	}
}
