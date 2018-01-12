/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.tests.gui;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.qaprosoft.zafira.tests.util.Config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractPage
{
	protected static final Logger LOGGER = Logger.getLogger(AbstractPage.class);
	protected static final Long IMPLICITLY_TIMEOUT = 15L;

	protected String url;
	
	protected WebDriver driver;

	public AbstractPage(WebDriver driver, String path)
	{
		this.driver = driver;
		this.url = Config.get("base_url") + path;
		PageFactory.initElements(driver, this);
	}
	
	public void open()
	{
		driver.get(url);
		driver.manage().window().maximize();
	}
	
	public boolean isOpened()
	{
		return new WebDriverWait(driver, 15).until(ExpectedConditions.urlMatches(url)).booleanValue();
	}

	public void reload(){
		driver.navigate().refresh();
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
		return !isElementPresent(By.cssSelector("md-menu.md-open.md-menu"), 1) && waitUntilElementToBeClickable(webElement, seconds);
	}

	public boolean waitUntilElementIsPresent(By by, long seconds)
	{
		return innerTimeoutOperation(() -> {
			WebDriverWait webDriverWait = new WebDriverWait(driver, seconds, 0L);
			webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
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

	public boolean waitUntilElementToBeClickable(WebElement webElement, long seconds)
	{
		return innerTimeoutOperation(() -> {
			WebDriverWait webDriverWait = new WebDriverWait(driver, seconds, 0L);
			webDriverWait.until(ExpectedConditions.elementToBeClickable(webElement));
			return webDriverWait;
		}, "Start to wait until element is clickable", "Finish to wait element");
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

	public void clickOutside()
	{
		clickByCoordinates("1", "1");
	}

	public void clickByCoordinates(String x, String y)
	{
		pause(1);
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript(String.format("$(document.elementFromPoint(%s, %s)).click();", x, y));
		pause(1);
	}

	public void pause(long timeout)
	{
		try
		{
			Thread.sleep(timeout * 1000);
		} catch (InterruptedException e)
		{
			LOGGER.error(e.getMessage());
		}
	}
}
