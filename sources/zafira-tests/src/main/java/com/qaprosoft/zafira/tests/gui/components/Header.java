package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import com.qaprosoft.zafira.tests.gui.components.modals.UploadImageModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.IntegrationsPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPerformancePage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Header extends AbstractUIObject
{

	@FindBy(id = "loading-bar-spinner")
	private WebElement loadingBarSpinner;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content[.//*[text() = 'Clear']]")
	private ProjectFilterMenu projectFilterMenu;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content[.//*[text() = 'My profile']]")
	private UserMenu userMenu;

	@FindBy(xpath = ".//*[contains(@class, 'logo-text')]")
	private WebElement zafiraLogo;

	@FindBy(xpath = ".//*[contains(@class, 'logo')]//img[contains(@class, 'img-circle')]")
	private WebElement companyLogo;

	@FindBy(xpath = ".//*[contains(@class, 'logo')]//i[text() = 'image']")
	private WebElement companyLogoBackgroundIcon;

	@FindBy(xpath = ".//*[contains(@class, 'logo')]//i[text() = 'settings']")
	private WebElement companyProfilePhotoHoverIcon;

	@FindBy(xpath = ".//button[.//*[text() = 'Project']]")
	private WebElement projectFilterButton;

	@FindBy(xpath = ".//md-menu[.//img | .//i[text() = 'account_circle']]")
	private WebElement userMenuButton;

	@FindBy(xpath = ".//*[@class='menu-button']")
	private WebElement mobileMenuButton;

	@FindBy(xpath = "//md-dialog")
	private UploadImageModalWindow uploadImageModalWindow;

	public Header(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getLoadingBarSpinner()
	{
		return loadingBarSpinner;
	}

	public ProjectFilterMenu getProjectFilterMenu()
	{
		return projectFilterMenu;
	}

	public UserMenu getUserMenu()
	{
		return userMenu;
	}

	public WebElement getZafiraLogo()
	{
		return zafiraLogo;
	}

	public WebElement getCompanyLogo()
	{
		return companyLogo;
	}

	public WebElement getCompanyLogoBackgroundIcon()
	{
		return companyLogoBackgroundIcon;
	}

	public WebElement getCompanyProfilePhotoHoverIcon()
	{
		return companyProfilePhotoHoverIcon;
	}

	public WebElement getProjectFilterButton()
	{
		return projectFilterButton;
	}

	public WebElement getUserMenuButton()
	{
		return userMenuButton;
	}

	public WebElement getMobileMenuButton()
	{
		return mobileMenuButton;
	}

	public UploadImageModalWindow clickCompanyPhotoHoverIcon()
	{
		companyProfilePhotoHoverIcon.click();
		LOGGER.info("Company logo icon was clicked");
		return uploadImageModalWindow;
	}

	public ProjectFilterMenu clickProjectFilterButton()
	{
		if (!isElementClickable(projectFilterButton, 10) || isElementPresent(getBackdrop(), 1))
			clickOutside();
		waitUntilElementToBeClickableByBackdropMask(this.projectFilterButton, 1);
		this.projectFilterButton.click();
		LOGGER.info("Project filter button was clicked");
		return projectFilterMenu;
	}

	public UserMenu clickUserMenuButton()
	{
		if (isElementPresent(getBackdrop(), 2) || !isElementClickable(userMenuButton, 2))
			clickOutside();
		waitUntilElementToBeClickableByBackdropMask(this.userMenuButton, 2);
		this.userMenuButton.click();
		LOGGER.info("User menu button was clicked");
		return userMenu;
	}

	public UserProfilePage goToUserProfilePage()
	{
		WebElement userProfileButton = this.clickUserMenuButton().getUserProfileButton();
		if (!isElementClickable(userProfileButton, 2) || isElementPresent(getBackdrop(), 1))
			waitUntilElementToBeClickableByBackdropMask(userProfileButton, 1);
		userProfileButton.click();
		LOGGER.info("Profile button was clicked");
		return new UserProfilePage(driver);
	}

	public UserPerformancePage goToUserPerformancePage()
	{
		WebElement userPerformanceButton = this.clickUserMenuButton().getUserPerformanceButton();
		if (!isElementClickable(userPerformanceButton, 2) || isElementPresent(getBackdrop(), 1))
			waitUntilElementToBeClickableByBackdropMask(userPerformanceButton, 1);
		userPerformanceButton.click();
		LOGGER.info("User performance button was clicked");
		return new UserPerformancePage(driver, PERFORMANCE_DASHBOARD_ID, ADMIN_ID);
	}

	public IntegrationsPage goToIntegrationsPage()
	{
		WebElement integrationsButton = this.clickUserMenuButton().getIntegrationsButton();
		if (!isElementClickable(integrationsButton, 2) || isElementPresent(getBackdrop(), 1))
			waitUntilElementToBeClickableByBackdropMask(integrationsButton, 1);
		integrationsButton.click();
		LOGGER.info("Integration button was clicked");
		return new IntegrationsPage(driver);
	}

	public LoginPage logOut()
	{
		WebElement logoutButton = this.clickUserMenuButton().getLogoutButton();
		if (!isElementClickable(logoutButton, 2) || isElementPresent(getBackdrop(), 1))
			waitUntilElementToBeClickableWithBackdropMask(logoutButton, 1);
		logoutButton.click();
		LOGGER.info("Logout button was clicked");
		return new LoginPage(driver);
	}
}
