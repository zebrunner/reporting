package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.components.modals.ChangePasswordModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateGroupModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateUserModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserPageService extends AbstractPageWithTableService
{

	private static final Integer USER_PHOTO_COLUMN_NUMBER = 1;
	private static final Integer USERNAME_COLUMN_NUMBER = 2;
	private static final Integer EMAIL_COLUMN_NUMBER = 3;
	private static final Integer FIRST_LAST_NAME_COLUMN_NUMBER = 4;
	private static final Integer STATUS_COLUMN_NUMBER = 5;
	private static final Integer REGISTRATION_AND_LAST_LOGIN_COLUMN_NUMBER = 6;

	private static final String CREATE_USER_BUTTON_CLASS = "fa-plus";
	private static final String CREATE_GROUP_BUTTON_CLASS = "fa-users";

	private UserPage userPage;

	public UserPageService(WebDriver driver)
	{
		super(driver);
		this.userPage = new UserPage(driver);
	}

	public WebElement getUserPhotoByIndex(int index)
	{
		return getTableColumnByIndex(index, USER_PHOTO_COLUMN_NUMBER).findElement(By.
				xpath("(//tbody//tr)[1]//td[1]//img[not(contains(@class, 'ng-hide'))] | (//tbody//tr)[1]//td[1]//i[not(contains(@class, 'ng-hide'))]"));
	}

	public String getUsernameByIndex(int index)
	{
		return getTableColumnByIndex(index, USERNAME_COLUMN_NUMBER).getText();
	}

	public String getEmailByIndex(int index)
	{
		return getTableColumnByIndex(index, EMAIL_COLUMN_NUMBER).getText();
	}

	public String getFirstLastNameByIndex(int index)
	{
		return getTableColumnByIndex(index, FIRST_LAST_NAME_COLUMN_NUMBER).getText();
	}

	public String getStatusByIndex(int index)
	{
		return getTableColumnByIndex(index, STATUS_COLUMN_NUMBER).getText();
	}

	public String getRegistrationDateByIndex(int index)
	{
		return getTableColumnByIndex(index, REGISTRATION_AND_LAST_LOGIN_COLUMN_NUMBER).findElement(By.tagName("span")).getText();
	}

	public String getLastLoginTextByIndex(int index)
	{
		return getTableColumnByIndex(index, REGISTRATION_AND_LAST_LOGIN_COLUMN_NUMBER).findElement(By.tagName("b")).getText();
	}

	public CreateUserModalWindow goToEditUserModalWindow(int index)
	{
		return clickUserMenuButtonByIndex(index).clickEditProfileButton();
	}

	public ChangePasswordModalWindow goToChangePasswordModalWindow(int index)
	{
		return clickUserMenuButtonByIndex(index).clickChangePasswordButton();
	}

	public DashboardPage goToPerformance(int index)
	{
		return clickUserMenuButtonByIndex(index).clickPerformanceButton();
	}

	public CreateUserModalWindow goToCreateUserModalWindow()
	{
		userPage.clickFabMenu();
		userPage.clickFabMenuButtonByClassName(CREATE_USER_BUTTON_CLASS);
		return new CreateUserModalWindow(driver, null);
	}

	public CreateGroupModalWindow goToCreateGroupModalWindow()
	{
		userPage.clickFabMenu();
		userPage.clickFabMenuButtonByClassName(CREATE_GROUP_BUTTON_CLASS);
		return new CreateGroupModalWindow(driver, null);
	}

	public UserPage search(String id, String username, String email, String firstLastName)
	{
		userPage.getUserSearchBlock().typeId(id);
		userPage.getUserSearchBlock().typeUsername(username);
		userPage.getUserSearchBlock().typeEmail(email);
		userPage.getUserSearchBlock().typeFirstLastName(firstLastName);
		userPage.getUserSearchBlock().clickSearchButton();
		userPage.waitUntilPageIsLoaded();
		return userPage;
	}

	public UserPage clearSearchForm()
	{
		userPage.getUserSearchBlock().clickClearButton();
		userPage.waitUntilPageIsLoaded();
		return userPage;
	}

	public UserPage goToFirstPage()
	{
		userPage.getPaginationBlock().clickFirstPageButton();
		userPage.waitUntilPageIsLoaded();
		return userPage;
	}

	public UserPage goToPreviousPage()
	{
		userPage.getPaginationBlock().clickPreviousPageButton();
		userPage.waitUntilPageIsLoaded();
		return userPage;
	}

	public UserPage goToNextPage()
	{
		userPage.getPaginationBlock().clickNextPageButton();
		userPage.waitUntilPageIsLoaded();
		return userPage;
	}

	public UserPage goToLastPage()
	{
		userPage.getPaginationBlock().clickLastPageButton();
		userPage.waitUntilPageIsLoaded();
		return userPage;
	}
}
