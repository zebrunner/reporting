package com.qaprosoft.zafira.tests.gui.components;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import com.qaprosoft.zafira.tests.gui.components.modals.UploadImageModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.IntegrationsPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPerformancePage;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;

public class Header extends AbstractUIObject
{

	@FindBy(id = "loading-bar-spinner")
	private WebElement loadingBarSpinner;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content[.//*[text() = 'Clear']]")
	private ProjectFilterMenu projectFilterMenu;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content[.//*[text() = 'My profile']]")
	private UserMenu userMenu;

	@FindBy(css = ".logo-icon img")
	private WebElement zafiraLogo;

	@FindBy(xpath = ".//*[contains(@class, 'logo')]//img[contains(@class, 'img-circle')]")
	private WebElement companyLogo;

	@FindBy(xpath = ".//*[@id = 'brandPhoto']//md-icon")
	private WebElement companyLogoBackgroundIcon;

	@FindBy(xpath = ".//*[contains(@class, 'profile-img')]//md-icon[contains(@md-svg-src, 'add')]")
	private WebElement companyProfilePhotoHoverIcon;

	@FindBy(xpath = ".//button[.//*[text() = 'Projects']]")
	private WebElement projectsFilterButton;

	@FindBy(xpath = ".//md-menu[.//*[@id = 'profileMenu']]")
	private WebElement userMenuButton;

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
		return projectsFilterButton;
	}

	public WebElement getUserMenuButton()
	{
		return userMenuButton;
	}

	public UploadImageModalWindow clickCompanyPhotoHoverIcon()
	{
		companyProfilePhotoHoverIcon.click();
		LOGGER.info("Company logo icon was clicked");
		return uploadImageModalWindow;
	}

	public ProjectFilterMenu clickProjectFilterButton()
	{
		if (!isElementClickable(projectsFilterButton, 10) || isBackdropPresent(1))
			clickOutside();
		waitUntilElementToBeClickableByBackdropMask(this.projectsFilterButton, 1);
		this.projectsFilterButton.click();
		LOGGER.info("Project filter button was clicked");
		return projectFilterMenu;
	}

	public UserMenu clickUserMenuButton()
	{
		if (isBackdropPresent(2) || !isElementClickable(userMenuButton, 2))
			clickOutside();
		waitUntilElementToBeClickableByBackdropMask(this.userMenuButton, 2);
		this.userMenuButton.click();
		LOGGER.info("User menu button was clicked");
		return userMenu;
	}

	public UserProfilePage goToUserProfilePage()
	{
		WebElement userProfileButton = this.clickUserMenuButton().getUserProfileButton();
		waitUntilElementToBeClickableWithBackdropMask(userProfileButton, 5);
		userProfileButton.click();
		LOGGER.info("Profile button was clicked");
		return new UserProfilePage(driver);
	}

	public UserPerformancePage goToUserPerformancePage()
	{
		WebElement userPerformanceButton = this.clickUserMenuButton().getUserPerformanceButton();
		//if (!isElementClickable(userPerformanceButton, 2) || isBackdropPresent(2))
		waitUntilElementToBeClickableWithBackdropMask(userPerformanceButton, 5);
		userPerformanceButton.click();
		LOGGER.info("User performance button was clicked");
		return new UserPerformancePage(driver, PERFORMANCE_DASHBOARD_ID, ADMIN_ID);
	}

	public IntegrationsPage goToIntegrationsPage()
	{
		WebElement integrationsButton = this.clickUserMenuButton().getIntegrationsButton();
		//if (!isElementClickable(integrationsButton, 2) || isBackdropPresent(1))
		waitUntilElementToBeClickableWithBackdropMask(integrationsButton, 5);
		integrationsButton.click();
		LOGGER.info("Integration button was clicked");
		return new IntegrationsPage(driver);
	}

	public LoginPage logOut()
	{
		WebElement logoutButton = this.clickUserMenuButton().getLogoutButton();
		if (!isElementClickable(logoutButton, 2) || isBackdropPresent(1))
			waitUntilElementToBeClickableWithBackdropMask(logoutButton, 1);
		logoutButton.click();
		LOGGER.info("Logout button was clicked");
		return new LoginPage(driver);
	}
}
