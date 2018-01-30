package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import com.qaprosoft.zafira.tests.services.api.TestRunAPIService;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import com.qaprosoft.zafira.tests.services.gui.TestRunPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestRunPageTest extends AbstractTest
{

	private TestRunAPIService testRunAPIService;
	private TestRunPageService testRunPageService;
	private TestRunPage testRunPage;

	@Autowired
	private TestRunMapper testRunMapper;

	@BeforeMethod
	public void setup()
	{
		LoginPage loginPage = new LoginPage(driver);
		LoginPageService loginPageService = new LoginPageService(driver);
		loginPage.open();
		DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		dashboardPage.waitUntilPageIsLoaded(10);
		testRunPage = dashboardPage.getNavbar().goToTestRunPage();
		testRunPage.waitUntilPageIsLoaded();
		testRunAPIService = new TestRunAPIService();
		testRunPageService = new TestRunPageService(driver);
	}

	@Test
	public void verifyNavigationTest()
	{
		Assert.assertEquals(testRunPage.getPageTitleText(), "Test runs", "Incorrect title");
		Assert.assertEquals(testRunPage.getPageItemsCount(), testRunMapper.getTestRunsSearchCount(new TestRunSearchCriteria()), "Incorrect title");
		//Assert.assertTrue(testRunPage.isElementPresent(testRunPageService.getUserMenuButtonByIndex(1), 1));

		//testRunPageService.getUserMenuButtonByIndex(1).click();

		//Assert.assertTrue(testRunPageService);
	}
}
