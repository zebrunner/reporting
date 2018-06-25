package com.qaprosoft.zafira.tests.gui.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import com.qaprosoft.zafira.tests.gui.components.Header;
import com.qaprosoft.zafira.tests.gui.components.Navbar;

public abstract class BasePage extends AbstractPage
{

	@FindBy(css = "h2.section-header")
	private WebElement pageTitle;

	@FindBy(xpath = "//*[@class = 'section-header']//small | //*[contains(@class, 'fixed-search-column')]//small")
	private WebElement pageItemsCount;

	@FindBy(xpath = "//md-fab-trigger")
	private WebElement fabButton;

	@FindBy(id = "header")
	private Header header;

	@FindBy(id = "nav-container")
	private Navbar navbar;

	public BasePage(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public Header getHeader()
	{
		return header;
	}

	public Navbar getNavbar()
	{
		return navbar;
	}

	public boolean waitUntilPageIsLoaded(long seconds)
	{
		LOGGER.info("Wait until page is loading");
		if(!isElementPresent(header.getZafiraLogo(), IMPLICITLY_TIMEOUT)){
			Assert.fail("Zafira is not loaded for " + IMPLICITLY_TIMEOUT + " seconds" );
		}
		return isElementPresent(header.getLoadingBarSpinner(), 1) &&
				waitUntilElementIsNotPresent(header.getLoadingBarSpinner(), seconds);
	}

    public boolean waitUntilLoadingContainerDisappears (long seconds){
		return new WebDriverWait(driver, seconds).until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader-container")));
	}

	public boolean waitUntilPageIsLoaded()
	{
		return waitUntilPageIsLoaded(IMPLICITLY_TIMEOUT);
	}

	public BasePage reload(){
		driver.navigate().refresh();
		waitUntilLoadingContainerDisappears(IMPLICITLY_TIMEOUT);
		waitUntilPageIsLoaded();
		LOGGER.info("Page was refreshed. Current url: " + driver.getCurrentUrl());
		return this;
	}

	public String getPageTitleText()
	{
		return pageTitle.getText();
	}

	public boolean isFabMenuPresent(int seconds)
	{
		return isElementPresent(fabButton, seconds);
	}

	public List<WebElement> getFabMenuButtons()
	{
		return fabButton.findElements(By.xpath(".//following-sibling::md-fab-actions//button"));
	}

	public Integer getPageItemsCount()
	{
		String text;
		waitUntilPageIsLoaded();
		if(isElementPresent(pageItemsCount, 2))
		{
			try
			{
				text = pageItemsCount.getText();
			} catch (Exception e)
			{
				text = "(0)";
			}
		} else
		{
			text = "(0)";
		}
		return Integer.valueOf(text.substring(text.indexOf("(") + 1, text.indexOf(")")));
	}

	public WebElement getFabMenuButtonByClassName(String classPartialText)
	{
		By fabMenuButtonLocator = By.xpath(".//following-sibling::md-fab-actions//button[.//span[contains(@class, '"
				+ classPartialText + "')]]");
		waitUntilElementIsPresent(fabButton, fabMenuButtonLocator, 2);
		return fabButton.findElement(fabMenuButtonLocator);
	}

	public void clickFabMenu()
	{
		fabButton.click();
		pause(0.5);
	}

	public void goToFabButtonByClassName(String className)
	{
		clickFabMenu();
		clickFabMenuButtonByClassName(className);
	}

	public void clickFabMenuButtonByClassName(String classPartialName)
	{
		pause(0.9);
		getFabMenuButtonByClassName(classPartialName).click();
	}
}
