package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.dbaccess.dao.mysql.UserMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.tests.gui.components.UserSettingMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.ChangePasswordModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateGroupModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateUserModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import com.qaprosoft.zafira.tests.services.api.UserAPIService;
import com.qaprosoft.zafira.tests.services.api.builders.UserTypeBuilder;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import com.qaprosoft.zafira.tests.services.gui.UserPageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

public class UserPageTest extends AbstractTest
{

	private UserPage userPage;
	private UserPageService userPageService;
	private LoginPageService loginPageService;

	@Autowired
	private UserMapper userMapper;

	@BeforeMethod
	public void setup()
	{
		LoginPage loginPage = new LoginPage(driver);
		loginPageService = new LoginPageService(driver);
		loginPage.open();
		DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		pause(2);
		dashboardPage.waitUntilPageIsLoaded(10);
		this.userPage = dashboardPage.getNavbar().clickUsersTab();
		this.userPage.waitUntilPageIsLoaded(10);
		this.userPageService = new UserPageService(driver);
	}

	@Test
	public void verifyUserPageNavigationTest()
	{
		Assert.assertTrue(userPage.isOpened(), "Users page not opened");
		Assert.assertTrue(userPage.getPageTitleText().contains("Users"), "Users page title is uncorrect");

		Assert.assertTrue(userPage.isFabMenuPresent(1));

		CreateUserModalWindow createUserModalWindow = userPageService.goToCreateUserModalWindow();
		Assert.assertEquals(createUserModalWindow.getHeaderText(), "Create user", "Create user modal window has an invalid title");
		createUserModalWindow.closeModalWindow();
		CreateGroupModalWindow createGroupModalWindow = userPageService.goToCreateGroupModalWindow();
		Assert.assertEquals(createGroupModalWindow.getHeaderText(), "Groups", "Create groups modal window has an invalid title");
		createGroupModalWindow.closeModalWindow();

		List<User> users = userMapper.searchUsers(new UserSearchCriteria());
		Assert.assertEquals(userPage.getUserRows().size() , users.size() >= 20 ? 20 : users.size(), "Count of user menu buttons is not 20");

		UserSettingMenu userSettingMenu = userPageService.clickUserMenuButtonByIdOrIndex(0, false);

		Assert.assertTrue(userSettingMenu.isElementPresent(1), "User settings menu is not present");
		Assert.assertTrue(userPage.isElementPresent(userSettingMenu.getEditProfileButton(), 1), "Edit profile button is not present");
		Assert.assertTrue(userPage.isElementPresent(userSettingMenu.getChangePasswordButton(), 1), "Change password button is not present");
		Assert.assertTrue(userPage.isElementPresent(userSettingMenu.getPerformanceButton(), 1), "Performance button is not present");

		createUserModalWindow = userPageService.goToEditUserModalWindow(0, false);
		Assert.assertTrue(createUserModalWindow.isElementPresent(4), "Edit user modal window not opened");
		Assert.assertEquals(createUserModalWindow.getHeaderText(), "Edit Profile", "Invalid header text on edit profile modal window");
		createUserModalWindow.closeModalWindow();

		ChangePasswordModalWindow changePasswordModalWindow = userPageService.goToChangePasswordModalWindow(0, false);
		Assert.assertTrue(changePasswordModalWindow.isElementPresent(4), "Change password modal window not opened");
		Assert.assertEquals(changePasswordModalWindow.getHeaderText(), "Change password", "Invalid header text on edit profile modal window");
		createUserModalWindow.closeModalWindow();

		DashboardPage dashboardPage = userPageService.goToPerformance(0, false);
		Assert.assertTrue(dashboardPage.isOpened(), "Dashboard page not opened");
		Assert.assertEquals(dashboardPage.getPageTitleText(), "User Performance", "Dashboard page not opened");
	}

	@Test
	public void verifyUsersInfoTest()
	{
		List<User> users = userMapper.searchUsers(new UserSearchCriteria());
		Assert.assertEquals(userPage.getUserRows().size() , users.size() >= 20 ? 20 : users.size(), "Count of user menu buttons is not 20");
		if(users.size() < 20)
		{
			Assert.assertEquals(userPage.getUserRows().size() , users.size(), "Count of user menu buttons is invalid");
			UserAPIService userAPIService = new UserAPIService();
			userAPIService.createUsers(25);
			userPage.reload();
			userPage.waitUntilPageIsLoaded(10);
			Assert.assertEquals(userPage.getUserRows().size() , 20, "Count of user menu buttons is not 20");
		} else {
			Assert.assertEquals(userPage.getUserRows().size() , 20, "Count of user menu buttons is not 20");
		}
		CreateUserModalWindow createUserModalWindow = userPageService.goToCreateUserModalWindow();
		UserType userType = (new UserTypeBuilder()).getUserType();
		userType.setUsername("1a" + (new Random()).nextInt(10000));
		userType.setPassword("Welcome1!");
		userPage = createUserModalWindow.registerUser(userType);
		Assert.assertEquals(userPage.getSuccessAlert().getText(), "User created", "Invalid user created alert");
		userPage.waitUntilPageIsLoaded(10);
		userType.setId(userMapper.getUserByUserName(userType.getUsername()).getId());
		Assert.assertEquals(userPage.getUserRows().size() , 20, "Count of user menu buttons is not 20");
		verifyUsersTableByRowIndex(userType, 1);

		createUserModalWindow = userPageService.goToEditUserModalWindow(1, false);
		verifyUpdateUserModalInputs(createUserModalWindow, userType);
		userType.setFirstName("newFirstName");
		userType.setLastName("newLastName");
		userType.setEmail("newEmail@test.com");
		createUserModalWindow.clearAllInputs();
		createUserModalWindow.updateUser(userType);
		verifyUsersTableByRowIndex(userType, 1);

		userPage.getHeader().logOut();
		DashboardPage dashboardPage = loginPageService.login(userType.getUsername(), "Welcome1!");
		Assert.assertTrue(dashboardPage.isOpened(), "Current page is not dashboard page");
		userPage = dashboardPage.getNavbar().clickUsersTab();
		userPage.waitUntilPageIsLoaded(10);
		verifyUsersTableByRowIndex(userType, 1);

		ChangePasswordModalWindow changePasswordModalWindow = userPageService.goToChangePasswordModalWindow(1, false);
		Assert.assertTrue(StringUtils.isBlank(changePasswordModalWindow.getWebElementValue(changePasswordModalWindow.getPasswordInput())), "Password input is not empty");
		Assert.assertTrue(StringUtils.isBlank(changePasswordModalWindow.getWebElementValue(changePasswordModalWindow.getConfirmPasswordInput())), "Confirm password input is not empty");
		Assert.assertTrue(changePasswordModalWindow.hasDisabledAttribute(changePasswordModalWindow.getChangeButton()), "Change button is not disabled");

		userPage = changePasswordModalWindow.changePassword("Welcome2!");
		Assert.assertEquals(userPage.getSuccessAlert().getText(), "Password changed", "Password changed alert is not present");
		LoginPage loginPage = userPage.getHeader().logOut();
		dashboardPage = loginPageService.login(userType.getUsername(), "Welcome2!");
		dashboardPage.waitUntilPageIsLoaded(10);
		Assert.assertTrue(dashboardPage.isOpened(), "Current page is not dashboards page");
		userPage = dashboardPage.getNavbar().clickUsersTab();
		userPage.waitUntilPageIsLoaded(10);

		createUserModalWindow = userPageService.goToEditUserModalWindow(1, false);
		createUserModalWindow.clickDeleteButton();
		Assert.assertEquals(userPage.getSuccessAlert().getText(), "User deleted", "User deleted alert is not present");
		Assert.assertNotEquals(userPageService.getUsernameByIdOrIndex(1, false), userType.getUsername(), "User presents after deleting");
	}

	public void verifyUsersTableByRowIndex(UserType userType, int index)
	{
		Assert.assertEquals(userPage.hoverAndGetTooltipText(userPageService.getUserPhotoByIdOrIndex(index, false)), "#" + userType.getId());
		Assert.assertEquals(userPageService.getUsernameByIdOrIndex(index, false), userType.getUsername(), "Invalid username");
		Assert.assertEquals(userPageService.getFirstLastNameByIdOrIndex(index, false), userType.getFirstName() + " " + userType.getLastName(), "Invalid first name");
		Assert.assertEquals(userPageService.getEmailByIdOrIndex(index, false), userType.getEmail(), "Invalid email");
		Assert.assertEquals(userPageService.getStatusByIdOrIndex(index, false), "Active", "Invalid email");
	}

	public void verifyUpdateUserModalInputs(CreateUserModalWindow createUserModalWindow, UserType userType)
	{
		Assert.assertTrue(createUserModalWindow.hasDisabledAttribute(createUserModalWindow.getUsernameInput()), "Username input is not disabled");
		Assert.assertEquals(createUserModalWindow.getWebElementValue(createUserModalWindow.getUsernameInput()), userType.getUsername(), "Username in input is invalid");
		Assert.assertEquals(createUserModalWindow.getWebElementValue(createUserModalWindow.getFirstNameInput()), userType.getFirstName(), "First name in input is invalid");
		Assert.assertEquals(createUserModalWindow.getWebElementValue(createUserModalWindow.getLastNameInput()), userType.getLastName(), "Last name in input is invalid");
		Assert.assertEquals(createUserModalWindow.getWebElementValue(createUserModalWindow.getEmailInput()), userType.getEmail(), "Email in input is invalid");
		Assert.assertTrue(createUserModalWindow.isElementPresent(createUserModalWindow.getUpdateButton(), 1), "Update button is not present");
		Assert.assertTrue(createUserModalWindow.isElementPresent(createUserModalWindow.getDeleteButton(), 1), "Delete button is not present");
	}

}
