package com.qaprosoft.zafira.tests.services.gui;

import org.openqa.selenium.WebDriver;

import com.qaprosoft.zafira.tests.gui.components.menus.UserSettingMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.ChangePasswordModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateGroupModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateUserModalWindow;
import com.qaprosoft.zafira.tests.gui.components.table.row.UserTableRow;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;

public class UserPageService extends AbstractPageService
{

	private static final String CREATE_USER_BUTTON_CLASS = "fa-plus";
	private static final String CREATE_GROUP_BUTTON_CLASS = "fa-users";

	private UserPage userPage;

	public UserPageService(WebDriver driver)
	{
		super(driver);
		this.userPage = new UserPage(driver);
	}

	public UserTableRow getUserTableRowByIndex(int index)
	{
		return userPage.getUserTable().getUserTableRows().get(index);
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

	public UserSettingMenu clickUserMenuButtonByIndex(int index)
	{
		UserTableRow userTableRow = getUserTableRowByIndex(index);
		if(userPage.isBackdropPresent(1))
		{
			userPage.clickOutside();
			userPage.waitUntilElementToBeClickableByBackdropMask(userTableRow.getUserSettingMenu().getRootElement(), 1);
		}
		UserSettingMenu userSettingMenu = userTableRow.clickUserSettingMenu();
		userSettingMenu.waitUntilElementToBeClickableWithBackdropMask(userSettingMenu.getRootElement(), 2);
		return userSettingMenu;
	}

	public CreateUserModalWindow goToCreateUserModalWindow()
	{
		userPage.clickFabMenu();
		userPage.clickFabMenuButtonByClassName(CREATE_USER_BUTTON_CLASS);
		return userPage.getCreateUserModalWindow();
	}

	public UserPage createUser(String email, String username, String password)
	{
		CreateUserModalWindow createUserModalWindow = goToCreateUserModalWindow();
		return createUserModalWindow.registerUser(username, "", "", email, password);
	}

	public CreateGroupModalWindow goToCreateGroupModalWindow()
	{
		userPage.clickFabMenu();
		userPage.clickFabMenuButtonByClassName(CREATE_GROUP_BUTTON_CLASS);
		return userPage.getCreateGroupModalWindow();
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

	public UserPage getUserPage()
	{
		return userPage;
	}

	public int getUserTableRowsCount()
	{
		return userPage.getUserTable().getUserTableRows().size();
	}
}
