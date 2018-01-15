package com.qaprosoft.zafira.tests.gui.pages;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.qaprosoft.zafira.tests.gui.components.Header;
import com.qaprosoft.zafira.tests.gui.components.Navbar;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public abstract class BasePage extends AbstractPage
{

	@FindBy(css = "h2.section-header")
	private WebElement pageTitle;

	@FindBy(tagName = "md-fab-trigger")
	private WebElement fabButton;

	private Header header;
	private Navbar navbar;

	protected BasePage(WebDriver driver, String path)
	{
		super(driver, path);
		this.header = new Header(driver, path);
		this.navbar = new Navbar(driver, path);
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
		return isElementPresent(header.getLoadingBarSpinnerLocator(), 1) &&
				waitUntilElementIsNotPresent(driver.findElement(header.getLoadingBarSpinnerLocator()), seconds);
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

	public WebElement getFabMenuButtonByClassName(String classPartialText)
	{
		return fabButton.findElement(By.xpath(".//following-sibling::md-fab-actions//button[.//span[contains(@class, '"
				+ classPartialText + "')]]"));
	}

	public void clickFabMenu()
	{
		fabButton.click();
	}

	public void clickFabMenuButtonByClassName(String classPartialName)
	{
		getFabMenuButtonByClassName(classPartialName).click();
	}
}
