package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Header extends AbstractPage
{

	private By loadingBarSpinnerLocator = By.id("loading-bar-spinner");

	@FindBy(xpath = "//header//*[contains(@class, 'logo-text')]")
	private WebElement logo;

	@FindBy(xpath = "//header//*[contains(@class, 'logo')]//img[contains(@class, 'img-circle')]")
	private WebElement companyImage;

	@FindBy(xpath = "//header//button[.//*[text() = 'Project']]")
	private WebElement projectSelect;

	@FindBy(xpath = "//header//button[.//img]")
	private WebElement profileNavSelect;

	@FindBy(xpath = "//header//*[@class='menu-button']")
	private WebElement mobileMenuButton;

	public Header(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public By getLoadingBarSpinnerLocator()
	{
		return loadingBarSpinnerLocator;
	}

	public WebElement getLogo()
	{
		return logo;
	}

	public WebElement getCompanyImage()
	{
		return companyImage;
	}

	public ProjectListContainer clickProjectSelect()
	{
		this.projectSelect.click();
		return new ProjectListContainer(driver, null);
	}

	public ProfileNavListContainer clickProfileNavSelect()
	{
		this.profileNavSelect.click();
		return new ProfileNavListContainer(driver, null);
	}

	public WebElement getProfileNavSelect()
	{
		return profileNavSelect;
	}

	public WebElement getMobileMenuButton()
	{
		return mobileMenuButton;
	}
}
