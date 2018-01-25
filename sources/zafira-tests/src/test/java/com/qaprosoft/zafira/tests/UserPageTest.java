package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.dbaccess.dao.mysql.UserMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.tests.gui.components.menus.UserSettingMenu;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class UserPageTest extends AbstractTest
{

	private static final String COUNT_OF_PAGE_ELEMENTS = "%s - %s of %s";

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
	public void verifyNavigationTest()
	{
		Assert.assertTrue(userPage.isOpened(), "Users page not opened");
		Assert.assertTrue(userPage.getPageTitleText().contains("Users"), "Users page title is uncorrect");

		Assert.assertTrue(userPage.isFabMenuPresent(1));

		CreateUserModalWindow createUserModalWindow = userPageService.goToCreateUserModalWindow();
		Assert.assertEquals(createUserModalWindow.getHeaderText(), "Create user",
				"Create user modal window has an invalid title");
		createUserModalWindow.closeModalWindow();
		CreateGroupModalWindow createGroupModalWindow = userPageService.goToCreateGroupModalWindow();
		Assert.assertEquals(createGroupModalWindow.getHeaderText(), "Groups",
				"Create groups modal window has an invalid title");
		createGroupModalWindow.closeModalWindow();

		List<User> users = userMapper.searchUsers(new UserSearchCriteria());
		Assert.assertEquals(userPage.getUserRows().size(), users.size() >= 20 ? 20 : users.size(),
				"Count of user menu buttons is not 20");

		UserSettingMenu userSettingMenu = userPageService.clickUserMenuButtonByIndex(1);

		Assert.assertTrue(userSettingMenu.isElementPresent(1), "User settings menu is not present");
		Assert.assertTrue(userPage.isElementPresent(userSettingMenu.getEditProfileButton(), 1),
				"Edit profile button is not present");
		Assert.assertTrue(userPage.isElementPresent(userSettingMenu.getChangePasswordButton(), 1),
				"Change password button is not present");
		Assert.assertTrue(userPage.isElementPresent(userSettingMenu.getPerformanceButton(), 1),
				"Performance button is not present");

		userPage.clickOutside();
		createUserModalWindow = userPageService.goToEditUserModalWindow(1);
		Assert.assertTrue(createUserModalWindow.isElementPresent(4), "Edit user modal window not opened");
		Assert.assertEquals(createUserModalWindow.getHeaderText(), "Edit Profile",
				"Invalid header text on edit profile modal window");
		createUserModalWindow.closeModalWindow();

		ChangePasswordModalWindow changePasswordModalWindow = userPageService.goToChangePasswordModalWindow(1);
		Assert.assertTrue(changePasswordModalWindow.isElementPresent(4), "Change password modal window not opened");
		Assert.assertEquals(changePasswordModalWindow.getHeaderText(), "Change password",
				"Invalid header text on edit profile modal window");
		createUserModalWindow.closeModalWindow();

		DashboardPage dashboardPage = userPageService.goToPerformance(1);
		Assert.assertTrue(dashboardPage.isOpened(), "Dashboard page not opened");
		Assert.assertEquals(dashboardPage.getPageTitleText(), "User Performance", "Dashboard page not opened");
	}

	@Test
	public void verifyInfoTest()
	{
		List<User> users = userMapper.searchUsers(new UserSearchCriteria());
		Assert.assertEquals(userPage.getUserRows().size(), users.size() >= 20 ? 20 : users.size(),
				"Count of user menu buttons is not 20");
		if (users.size() < 20)
		{
			Assert.assertEquals(userPage.getUserRows().size(), users.size(), "Count of user menu buttons is invalid");
			UserAPIService userAPIService = new UserAPIService();
			userAPIService.createUsers(25);
			userPage.reload();
			userPage.waitUntilPageIsLoaded(10);
			Assert.assertEquals(userPage.getUserRows().size(), 20, "Count of user menu buttons is not 20");
		} else
		{
			Assert.assertEquals(userPage.getUserRows().size(), 20, "Count of user menu buttons is not 20");
		}
		CreateUserModalWindow createUserModalWindow = userPageService.goToCreateUserModalWindow();
		UserType userType = (new UserTypeBuilder()).getUserType();
		userType.setUsername("1a" + (new Random()).nextInt(10000));
		userType.setPassword("Welcome1!");
		userPage = createUserModalWindow.registerUser(userType);
		Assert.assertEquals(userPage.getSuccessAlert().getText(), "User created", "Invalid user created alert");
		userPage.waitUntilPageIsLoaded(10);
		userType.setId(userMapper.getUserByUserName(userType.getUsername()).getId());
		Assert.assertEquals(userPage.getUserRows().size(), 20, "Count of user menu buttons is not 20");
		verifyUsersTableByRowIndex(userType, 1);

		createUserModalWindow = userPageService.goToEditUserModalWindow(1);
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
		userPage.getHeader().logOut();
		dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		dashboardPage.waitUntilPageIsLoaded(10);
		userPage = dashboardPage.getNavbar().clickUsersTab();
		userPage.waitUntilPageIsLoaded(10);
		verifyUsersTableByRowIndex(userType, 1);

		ChangePasswordModalWindow changePasswordModalWindow = userPageService.goToChangePasswordModalWindow(1);
		Assert.assertTrue(StringUtils
						.isBlank(changePasswordModalWindow.getWebElementValue(changePasswordModalWindow.getPasswordInput())),
				"Password input is not empty");
		Assert.assertTrue(StringUtils.isBlank(
				changePasswordModalWindow.getWebElementValue(changePasswordModalWindow.getConfirmPasswordInput())),
				"Confirm password input is not empty");
		Assert.assertTrue(changePasswordModalWindow.hasDisabledAttribute(changePasswordModalWindow.getChangeButton()),
				"Change button is not disabled");

		userPage = changePasswordModalWindow.changePassword("Welcome2!");
		userPage.waitUntilPageIsLoaded(10);
		Assert.assertEquals(userPage.getSuccessAlert().getText(), "Password changed",
				"Password changed alert is not present");
		userPage.getHeader().logOut();
		dashboardPage = loginPageService.login(userType.getUsername(), "Welcome2!");
		dashboardPage.waitUntilPageIsLoaded(10);
		Assert.assertTrue(dashboardPage.isOpened(), "Current page is not dashboards page");
		userPage.getHeader().logOut();
		dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		dashboardPage.waitUntilPageIsLoaded(10);
		userPage = dashboardPage.getNavbar().clickUsersTab();
		userPage.waitUntilPageIsLoaded(10);

		createUserModalWindow = userPageService.goToEditUserModalWindow(1);
		createUserModalWindow.clickDeleteButton();
		Assert.assertEquals(userPage.getSuccessAlert().getText(), "User deleted", "User deleted alert is not present");
		userPage.waitUntilPageIsLoaded(10);
		Assert.assertNotEquals(userPageService.getUsernameByIndex(1), userType.getUsername(),
				"User presents after deleting");
	}

	@Test
	public void verifySearchTest() throws ExecutionException, InterruptedException
	{
		CompletableFuture<List<UserType>> usersCompletableFuture = generateUsersIfExists(25);
		verifySearchBlockInputsAreEmpty();
		userPage.reload();
		userPage.waitUntilPageIsLoaded();
		List<UserType> users = usersCompletableFuture.get();
		UserType testUser = users.get(0);
		userPage = userPageService.search(Long.valueOf(testUser.getId()).toString(), "", "", "");
		verifyUsersTableByRowIndex(testUser.getId(), testUser.getUsername(), testUser.getEmail(), testUser.getFirstName(), testUser.getLastName(), 1);
		userPage.getUserSearchBlock().clearAllInputs();
		userPage = userPageService.search("", testUser.getUsername(), "", "");
		verifyUsersTableByRowIndex(testUser.getId(), testUser.getUsername(), testUser.getEmail(), testUser.getFirstName(), testUser.getLastName(), 1);
		userPage.getUserSearchBlock().clearAllInputs();
		userPage = userPageService.search("", "", testUser.getEmail(), "");
		verifyUsersTableByRowIndex(testUser.getId(), testUser.getUsername(), testUser.getEmail(), testUser.getFirstName(), testUser.getLastName(), 1);
		userPage.getUserSearchBlock().clearAllInputs();
		userPage = userPageService.search("", "", "", testUser.getFirstName());
		verifyUsersTableByRowIndex(testUser.getId(), testUser.getUsername(), testUser.getEmail(), testUser.getFirstName(), testUser.getLastName(), 1);
		userPage.getUserSearchBlock().clearAllInputs();
		userPage = userPageService.search("", "", "", testUser.getLastName());
		verifyUsersTableByRowIndex(testUser.getId(), testUser.getUsername(), testUser.getEmail(), testUser.getFirstName(), testUser.getLastName(), 1);
		userPage.getUserSearchBlock().clearAllInputs();
		userPageService.clearSearchForm();
		verifySearchBlockInputsAreEmpty();
	}

	@Test
	public void verifyPaginationTest() throws ExecutionException, InterruptedException
	{
		CompletableFuture<List<UserType>> completableFuture = generateUsersIfExists(60);
		Assert.assertTrue(userPage.hasDisabledAttribute(userPage.getPaginationBlock().getFirstPageButton()), "First page button is not disabled");
		Assert.assertTrue(userPage.hasDisabledAttribute(userPage.getPaginationBlock().getPreviousPageButton()), "Previous page button is not disabled");
		if(userPage.getPageItemsCount() > 20)
		{
			Assert.assertFalse(userPage.hasDisabledAttribute(userPage.getPaginationBlock().getNextPageButton()),
					"Next page button is not disabled");
			Assert.assertFalse(userPage.hasDisabledAttribute(userPage.getPaginationBlock().getLastPageButton()),
					"Last page button is not disabled");
		} else
		{
			Assert.assertTrue(userPage.hasDisabledAttribute(userPage.getPaginationBlock().getNextPageButton()),
					"Next page button is not disabled");
			Assert.assertTrue(userPage.hasDisabledAttribute(userPage.getPaginationBlock().getLastPageButton()),
					"Last page button is not disabled");
		}
		completableFuture.get();
		userPage.reload();
		userPage.waitUntilPageIsLoaded();
		int totalCount = userMapper.getUserSearchCount(new UserSearchCriteria());
		Assert.assertEquals(userPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, 1, 20, totalCount), "Count of user menu buttons is not 20");
		userPageService.goToNextPage();
		Assert.assertEquals(userPage.getUserRows().size(), 20, "Count of user menu buttons is not 20");
		Assert.assertEquals(userPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, 21, 40, totalCount), "Count of user menu buttons is not 20");
		userPageService.goToPreviousPage();
		Assert.assertEquals(userPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, 1, 20, totalCount), "Count of user menu buttons is not 20");
		userPageService.goToLastPage();
		Assert.assertEquals(userPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, totalCount - totalCount % 20 + 1, totalCount, totalCount), "Count of user menu buttons is not 20");
		userPageService.goToFirstPage();
		Assert.assertEquals(userPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, 1, 20, totalCount), "Count of user menu buttons is not 20");
	}

	private void verifySearchBlockInputsAreEmpty()
	{
		Assert.assertTrue(userPage.getUserSearchBlock().getIdInputValue().isEmpty(), "Id input is not empty");
		Assert.assertTrue(userPage.getUserSearchBlock().getUsernameValue().isEmpty(), "Username input is not empty");
		Assert.assertTrue(userPage.getUserSearchBlock().getEmailValue().isEmpty(), "Email input is not empty");
		Assert.assertTrue(userPage.getUserSearchBlock().getFirstLastNameValue().isEmpty(), "First lat name input is not empty");
	}

	private void verifyUsersTableByRowIndex(UserType userType, int index)
	{
		verifyUsersTableByRowIndex(userType.getId(), userType.getUsername(), userType.getEmail(), userType.getFirstName(), userType.getLastName(), index);
	}

	private void verifyUsersTableByRowIndex(long id, String username, String email, String firstName, String lastName, int index)
	{
		email = email == null ? "" : email;
		firstName = firstName == null ? "" : firstName + " ";
		lastName = lastName == null ? "" : lastName;
		Assert.assertEquals(userPage.hoverAndGetTooltipText(userPageService.getUserPhotoByIndex(index)),
				"#" + id);
		Assert.assertEquals(userPageService.getUsernameByIndex(index), username,
				"Invalid username");
		Assert.assertEquals(userPageService.getFirstLastNameByIndex(index),
				firstName + lastName, "Invalid first name");
		Assert.assertEquals(userPageService.getEmailByIndex(index), email, "Invalid email");
		//Assert.assertEquals(userPageService.getStatusByIndex(index), fromUI ? "Active" : "Inactive", "Invalid email");
	}

	private void verifyUpdateUserModalInputs(CreateUserModalWindow createUserModalWindow, UserType userType)
	{
		Assert.assertTrue(createUserModalWindow.hasDisabledAttribute(createUserModalWindow.getUsernameInput()),
				"Username input is not disabled");
		Assert.assertEquals(createUserModalWindow.getWebElementValue(createUserModalWindow.getUsernameInput()),
				userType.getUsername(), "Username in input is invalid");
		Assert.assertEquals(createUserModalWindow.getWebElementValue(createUserModalWindow.getFirstNameInput()),
				userType.getFirstName(), "First name in input is invalid");
		Assert.assertEquals(createUserModalWindow.getWebElementValue(createUserModalWindow.getLastNameInput()),
				userType.getLastName(), "Last name in input is invalid");
		Assert.assertEquals(createUserModalWindow.getWebElementValue(createUserModalWindow.getEmailInput()),
				userType.getEmail(), "Email in input is invalid");
		Assert.assertTrue(createUserModalWindow.isElementPresent(createUserModalWindow.getUpdateButton(), 1),
				"Update button is not present");
		Assert.assertTrue(createUserModalWindow.isElementPresent(createUserModalWindow.getDeleteButton(), 1),
				"Delete button is not present");
	}

	private CompletableFuture<List<UserType>> generateUsersIfExists(int count)
	{
		return CompletableFuture.supplyAsync(() -> {
			UserAPIService userAPIService = new UserAPIService();
			int currentCount = userPage.getPageItemsCount();
			return userAPIService.createUsers(currentCount <= 20 ? count : 1);
		});
	}
}
