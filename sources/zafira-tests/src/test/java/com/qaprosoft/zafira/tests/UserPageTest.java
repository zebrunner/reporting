package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.dbaccess.dao.mysql.UserMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.User;
import com.qaprosoft.zafira.tests.gui.components.modals.AbstractModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateGroupModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateUserModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.UserPage;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class UserPageTest extends AbstractTest
{

	private UserPage userPage;

	private static final String CREATE_USER_BUTTON_CLASS = "fa-plus";
	private static final String CREATE_GROUP_BUTTON_CLASS = "fa-users";

	@Autowired
	private UserMapper userMapper;

	@BeforeMethod
	public void setup()
	{
		LoginPageService loginPageService = new LoginPageService(driver);
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		pause(2);
		dashboardPage.waitUntilPageIsLoaded(10);
		this.userPage = dashboardPage.getNavbar().clickUsersTab();
		this.userPage.waitUntilPageIsLoaded(10);
	}

	@Test
	public void verifyUserPageTest()
	{
		Assert.assertTrue(this.userPage.isOpened(), "Users page not opened");
		Assert.assertTrue(this.userPage.getPageTitleText().contains("Users"), "Users page title is uncorrect");

		Assert.assertTrue(userPage.isFabMenuPresent(1));
		userPage.clickFabMenu();
		Assert.assertTrue(userPage.isElementPresent(userPage.getFabMenuButtonByClassName(CREATE_USER_BUTTON_CLASS), 1), "Create user fab button is not present");
		Assert.assertTrue(userPage.isElementPresent(userPage.getFabMenuButtonByClassName(CREATE_GROUP_BUTTON_CLASS), 1), "Create group fab button is not present");
		userPage.clickFabMenuButtonByClassName(CREATE_USER_BUTTON_CLASS);
		CreateUserModalWindow createUserModalWindow = new CreateUserModalWindow(driver);
		Assert.assertEquals(createUserModalWindow.getHeaderText(), "Create user", "Create user modal window has an invalid title");
		createUserModalWindow.closeModalWindow();
		userPage.clickFabMenu();
		userPage.clickFabMenuButtonByClassName(CREATE_GROUP_BUTTON_CLASS);
		CreateGroupModalWindow createGroupModalWindow = new CreateGroupModalWindow(driver);
		Assert.assertEquals(createGroupModalWindow.getHeaderText(), "Groups", "Create groups modal window has an invalid title");
		createGroupModalWindow.closeModalWindow();

		List<User> users = userMapper.searchUsers(new UserSearchCriteria());
		Assert.assertEquals(userPage.getUserRows().size() , users.size() >= 20 ? 20 : users.size(), "Count of user menu buttons is not 20");

		User user = users.get(0);
		userPage.getUserMenuButtonById(user.getId()).click();
	}
}
