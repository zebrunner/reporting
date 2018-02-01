package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import com.qaprosoft.zafira.tests.models.TestRunViewType;
import com.qaprosoft.zafira.tests.services.api.TestRunAPIService;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import com.qaprosoft.zafira.tests.services.gui.TestRunPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestRunPageTest extends AbstractTest
{

	private TestRunAPIService testRunAPIService;
	private TestRunPageService testRunPageService;
	private TestRunPage testRunPage;

	@Autowired
	private TestRunMapper testRunMapper;

	@BeforeMethod
	public void setup() throws ExecutionException, InterruptedException
	{
		int searchCount = testRunMapper.getTestRunsSearchCount(new TestRunSearchCriteria());
		CompletableFuture<List<TestRunViewType>> testRunFuture = generateTestRunsIfNeed(searchCount, 25);
		List<TestRunViewType> testRunViewTypes = testRunFuture.get();
		testRunAPIService = new TestRunAPIService();
		testRunPageService = new TestRunPageService(driver);
		LoginPage loginPage = new LoginPage(driver);
		LoginPageService loginPageService = new LoginPageService(driver);
		loginPage.open();
		DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		dashboardPage.waitUntilPageIsLoaded(10);
		testRunPage = dashboardPage.getNavbar().goToTestRunPage();
		testRunPage.waitUntilPageIsLoaded();
	}

	@Test
	public void verifyNavigationTest() throws Exception
	{
		int searchCount = testRunMapper.getTestRunsSearchCount(new TestRunSearchCriteria());
		CompletableFuture<List<TestRunViewType>> testRunFuture = generateTestRunsIfNeed(searchCount, 25);
		List<TestRunViewType> testRunViewTypes = testRunFuture.get();
		testRunPage.reload();
		Assert.assertTrue(testRunPage.getPageTitleText().contains("Test runs"), "Incorrect title");
		Assert.assertEquals(testRunPage.getPageItemsCount(), testRunMapper.getTestRunsSearchCount(new TestRunSearchCriteria()), "Incorrect title");
	}

	public CompletableFuture<List<TestRunViewType>> generateTestRunsIfNeed(Integer searchCount, int count)
	{
		return CompletableFuture.supplyAsync(() -> {
			TestRunAPIService testRunAPIService = new TestRunAPIService();
			int currentCount = searchCount == null ? testRunPage.getPageItemsCount() : searchCount;
			return testRunAPIService.createTestRuns(currentCount <= count ? count - currentCount : 1,
					2, 2, 2, 2, 2);
		});
	}
}
