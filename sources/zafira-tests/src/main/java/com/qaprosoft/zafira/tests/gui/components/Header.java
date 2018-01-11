package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Header extends AbstractPage implements IElement
{

	private static final String CONTAINER_LOCATOR = "header";

	@FindBy(id = CONTAINER_LOCATOR)
	private WebElement container;

	private By loadingBarSpinnerLocator = By.id("loading-bar-spinner");

	private ProjectFilter projectFilter;

	@FindBy(xpath = "//header//*[contains(@class, 'logo-text')]")
	private WebElement zafiraLogo;

	@FindBy(xpath = "//header//*[contains(@class, 'logo')]//img[contains(@class, 'img-circle')]")
	private WebElement companyLogo;

	@FindBy(xpath = "//header//button[.//*[text() = 'Project']]")
	private WebElement projectFilterButton;

	@FindBy(xpath = "//header//button[.//img]")
	private WebElement userMenuButton;

	@FindBy(xpath = "//header//*[@class='menu-button']")
	private WebElement mobileMenuButton;

	public Header(WebDriver driver, String path)
	{
		super(driver, path);
		this.projectFilter = new ProjectFilter(driver, path);
	}

	public By getLoadingBarSpinnerLocator()
	{
		return loadingBarSpinnerLocator;
	}

	public WebElement getZafiraLogo()
	{
		return zafiraLogo;
	}

	public WebElement getCompanyLogo()
	{
		return companyLogo;
	}

	public ProjectFilter clickProjectFilterButton()
	{
		this.projectFilterButton.click();
		return new ProjectFilter(driver, null);
	}

	public UserMenu clickUserMenuButton()
	{
		this.userMenuButton.click();
		return new UserMenu(driver, null);
	}

	public WebElement getUserMenuButton()
	{
		return userMenuButton;
	}

	public WebElement getMobileMenuButton()
	{
		return mobileMenuButton;
	}

	@Override
	public By getLocator()
	{
		return By.id(CONTAINER_LOCATOR);
	}

	@Override
	public WebElement getElement()
	{
		return this.container;
	}
}
