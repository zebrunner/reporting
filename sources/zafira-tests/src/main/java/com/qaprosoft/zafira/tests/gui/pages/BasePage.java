package com.qaprosoft.zafira.tests.gui.pages;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import com.qaprosoft.zafira.tests.gui.components.Header;
import com.qaprosoft.zafira.tests.gui.components.Navbar;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class BasePage extends AbstractPage
{

	@FindBy(css = "h2.section-header")
	private WebElement pageTitle;

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
}
