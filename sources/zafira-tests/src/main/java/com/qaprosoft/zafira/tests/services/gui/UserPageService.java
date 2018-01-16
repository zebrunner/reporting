package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.components.UserSettingMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.ChangePasswordModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateGroupModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateUserModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserPageService extends AbstractPageService
{

	public static final Integer USER_PHOTO_COLUMN_NUMBER = 1;
	public static final Integer USERNAME_COLUMN_NUMBER = 2;
	public static final Integer EMAIL_COLUMN_NUMBER = 3;
	public static final Integer FIRST_LAST_NAME_COLUMN_NUMBER = 4;
	public static final Integer STATUS_COLUMN_NUMBER = 5;
	public static final Integer REGISTRATION_AND_LAST_LOGIN_COLUMN_NUMBER = 6;
	public static final Integer USER_MENU_COLUMN_NUMBER = 7;

	private static final String CREATE_USER_BUTTON_CLASS = "fa-plus";
	private static final String CREATE_GROUP_BUTTON_CLASS = "fa-users";

	private UserPage userPage;

	public UserPageService(WebDriver driver)
	{
		super(driver);
		this.userPage = new UserPage(driver);
	}

	public WebElement getUserRowByUserId(long id)
	{
		return driver.findElement(By.xpath(".//tbody//tr[.//*[@aria-label = '#" + id + "']]"));
	}

	public WebElement getTableRowByIndex(long index)
	{
		return driver.findElement(By.xpath("(.//tbody//tr)[" + index + "]"));
	}

	public WebElement getUserPhotoByIdOrIndex(long id, boolean byId)
	{
		return getTableColumnByIdOrIndex(id, USER_PHOTO_COLUMN_NUMBER, byId).findElement(By.
				xpath("(//tbody//tr)[1]//td[1]//img[not(contains(@class, 'ng-hide'))] | (//tbody//tr)[1]//td[1]//i[not(contains(@class, 'ng-hide'))]"));
	}

	public String getUsernameByIdOrIndex(long id, boolean byId)
	{
		return getTableColumnByIdOrIndex(id, USERNAME_COLUMN_NUMBER, byId).getText();
	}

	public String getEmailByIdOrIndex(long id, boolean byId)
	{
		return getTableColumnByIdOrIndex(id, EMAIL_COLUMN_NUMBER, byId).getText();
	}

	public String getFirstLastNameByIdOrIndex(long id, boolean byId)
	{
		return getTableColumnByIdOrIndex(id, FIRST_LAST_NAME_COLUMN_NUMBER, byId).getText();
	}

	public String getStatusByIdOrIndex(long id, boolean byId)
	{
		return getTableColumnByIdOrIndex(id, STATUS_COLUMN_NUMBER, byId).getText();
	}

	public String getRegistrationDateByIdOrIndex(long id, boolean byId)
	{
		return getTableColumnByIdOrIndex(id, REGISTRATION_AND_LAST_LOGIN_COLUMN_NUMBER, byId).findElement(By.tagName("span")).getText();
	}

	public String getLastLoginTextByIdOrIndex(long id, boolean byId)
	{
		return getTableColumnByIdOrIndex(id, REGISTRATION_AND_LAST_LOGIN_COLUMN_NUMBER, byId).findElement(By.tagName("b")).getText();
	}

	public WebElement getUserMenuButtonByIdOrIndex(long id, boolean byId)
	{
		return getTableColumnByIdOrIndex(id, USER_MENU_COLUMN_NUMBER, byId).findElement(By.tagName("button"));
	}

	public UserSettingMenu clickUserMenuButtonByIdOrIndex(long id, boolean byId)
	{
		getUserMenuButtonByIdOrIndex(id, byId).click();
		UserSettingMenu userSettingMenu = new UserSettingMenu(driver);
		userSettingMenu.waitUntilElementToBeClickableByBackdropMask(userSettingMenu.getElement(), 1);
		return userSettingMenu;
	}

	public CreateUserModalWindow goToEditUserModalWindow(long userId, boolean byId)
	{
		return clickUserMenuButtonByIdOrIndex(userId, byId).clickEditProfileButton();
	}

	public ChangePasswordModalWindow goToChangePasswordModalWindow(long userId, boolean byId)
	{
		return clickUserMenuButtonByIdOrIndex(userId, byId).clickChangePasswordButton();
	}

	public DashboardPage goToPerformance(long userId, boolean byId)
	{
		return clickUserMenuButtonByIdOrIndex(userId, byId).clickPerformanceButton();
	}

	public CreateUserModalWindow goToCreateUserModalWindow()
	{
		userPage.clickFabMenu();
		userPage.clickFabMenuButtonByClassName(CREATE_USER_BUTTON_CLASS);
		return new CreateUserModalWindow(driver);
	}

	public CreateGroupModalWindow goToCreateGroupModalWindow()
	{
		userPage.clickFabMenu();
		userPage.clickFabMenuButtonByClassName(CREATE_GROUP_BUTTON_CLASS);
		return new CreateGroupModalWindow(driver);
	}

	public WebElement getTableColumnByIdOrIndex(long id, int columnNumber, boolean byId)
	{
		WebElement userRow = byId ? getUserRowByUserId(id) : getTableRowByIndex(id);
		return userRow.findElement(By.xpath(".//td[" + columnNumber + "]"));
	}
}
