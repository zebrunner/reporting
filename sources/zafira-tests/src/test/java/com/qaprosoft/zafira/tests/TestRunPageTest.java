package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.dbaccess.dao.mysql.TestMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestSearchCriteria;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.tests.gui.components.Chip;
import com.qaprosoft.zafira.tests.gui.components.menus.TestRunSettingMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.BuildNowModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.MarkAsReviewedModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.RebuildModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.SendAsEmailModalWindow;
import com.qaprosoft.zafira.tests.gui.components.table.TestTable;
import com.qaprosoft.zafira.tests.gui.components.table.row.TestRow;
import com.qaprosoft.zafira.tests.gui.components.table.row.TestRunTableRow;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import com.qaprosoft.zafira.tests.models.TestRunViewType;
import com.qaprosoft.zafira.tests.services.api.TestRunAPIService;
import com.qaprosoft.zafira.tests.services.api.UserAPIService;
import com.qaprosoft.zafira.tests.services.api.builders.TestRunTypeBuilder;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import com.qaprosoft.zafira.tests.services.gui.TestRunPageService;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class TestRunPageTest extends AbstractTest
{

	private TestRunPageService testRunPageService;
	private TestRunPage testRunPage;

	@Autowired
	private TestRunMapper testRunMapper;

	@Autowired
	private TestMapper testMapper;

	@BeforeMethod
	public void setup() throws ExecutionException, InterruptedException
	{
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
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage.reload();
		Assert.assertTrue(testRunPage.getPageTitleText().contains("Test runs"), "Incorrect title");
		Assert.assertEquals(testRunPage.getPageItemsCount(), testRunMapper.getTestRunsSearchCount(new TestRunSearchCriteria()), "Incorrect title");
		Assert.assertFalse(testRunPage.isFabMenuPresent(1), "Fab button is present");

		TestRunTableRow testRunTableRow = testRunPageService.getTestRunRowByIndex(0);
		testRunTableRow.checkCheckbox();
		Assert.assertTrue(testRunPage.isElementPresent(testRunPage.getFabButton().getButtonTrigger(), 1), "Fab button is not present");
		testRunPage.getFabButton().clickButtonTrigger();
		Assert.assertNotNull(testRunPage.getFabButton().getButtonMiniByClassName("trash"), "Delete fab button is not present");
		Assert.assertNotNull(testRunPage.getFabButton().getButtonMiniByClassName("ban"), "Abort fab button is not present");
		testRunPage.clickOutside();
		testRunTableRow.uncheckCheckbox();
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getOpenButton(), 1), "Open button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getCopyLinkButton(), 1), "Copy button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getMarkAsReviewedButton(), 1), "Mark as reviewed button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getSendAsEmailButton(), 1), "Send as email button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getExportButton(), 1), "Export button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getBuildNowButton(), 1), "Build button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getRebuildButton(), 1), "Rebuild button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getDeleteButton(), 1), "Delete button is not visible");
		testRunPage.clickOutside();
		testRunPage.getTestRunSearchBlock().checkMainCheckbox();
		testRunPage.getTestRunTable().getTestRunTableRows().forEach(row -> Assert.assertTrue(row.isChecked(row.getCheckbox()), "Some checkboxes are not checked"));
	}

	@Test
	public void verifyTestRunOpenTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		TestRunTableRow testRunTableRow = testRunPageService.getTestRunRowByIndex(0);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunSettingMenu.clickOpenButton();
		testRunSettingMenu.switchToWindow();
		testRunPage.waitUntilPageIsLoaded();
		Assert.assertEquals(testRunPage.getTestRunTable().getTestRunTableRows().size(), 1, "Invalid page was opened");
		String[] urlSplit = driver.getCurrentUrl().split("/");
		Assert.assertEquals(urlSplit[urlSplit.length - 1], String.valueOf(testRunViewTypes.get(0).getTestRunType().getId()), "Invalid test run was opened. "
				+ "Current url: " + driver.getCurrentUrl() + ", but test run id: " + testRunViewTypes.get(0).getTestRunType().getId());
	}

	@Test
	public void verifyTestRunCopyLinkTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		TestRunTableRow testRunTableRow = testRunPageService.getTestRunRowByIndex(0);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunSettingMenu.clickCopyLinkButton();
		testRunPage.getTestRunSearchBlock().getAppVersionInput().sendKeys(Keys.CONTROL + "v");
		String url = testRunPage.getWebElementValue(testRunPage.getTestRunSearchBlock().getAppVersionInput());
		String[] urlSplit = url.split("/");
		Assert.assertEquals(urlSplit[urlSplit.length - 1], String.valueOf(testRunViewTypes.get(0).getTestRunType().getId()), "Invalid test run was opened. "
				+ "Current url: " + url + ", but test run id: " + testRunViewTypes.get(0).getTestRunType().getId());
	}

	@Test
	public void verifyMarkAsReviewedTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		MarkAsReviewedModalWindow markAsReviewedModalWindow = testRunPageService.clickMarkAsReviewedButton(0);
		Assert.assertEquals(markAsReviewedModalWindow.getHeaderText(), "Comments", "Incorrect modal header text");
		Assert.assertTrue(markAsReviewedModalWindow.getMarkAsReviewedButton().isDisplayed(), "Mark as reviewed button is enabled");
		Assert.assertTrue(markAsReviewedModalWindow.getWebElementValue(markAsReviewedModalWindow.getCommentInput()).isEmpty(), "Comment input is not empty");
		markAsReviewedModalWindow.typeComment("Test");
		markAsReviewedModalWindow.clickMarkAsReviewedButton();
		markAsReviewedModalWindow.waitUntilElementToBeClickableWithBackdropMask(markAsReviewedModalWindow.getCommentInput(), 2);
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Test run #" + testRunViewTypes.get(0).getTestRunType().getId() + " marked as reviewed");
		Assert.assertFalse(markAsReviewedModalWindow.isElementPresent(1));
		TestRunTableRow testRunTableRow = testRunPageService.getTestRunRowByIndex(0);
		Assert.assertTrue(testRunTableRow.isElementPresent(testRunTableRow.getCommentIcon(), 1), "Comment icon is not displayed");
		testRunPage = (TestRunPage) testRunPage.reload();
		testRunTableRow = testRunPageService.getTestRunRowByIndex(0);
		Assert.assertTrue(testRunTableRow.isElementPresent(testRunTableRow.getReviewedLabel(), 1), "Reviewed label is not displayed");
		markAsReviewedModalWindow = testRunPageService.clickCommentIcon(0);
		Assert.assertEquals(markAsReviewedModalWindow.getHeaderText(), "Comments", "Incorrect modal header text");
		Assert.assertEquals(markAsReviewedModalWindow.getWebElementValue(markAsReviewedModalWindow.getCommentInput()), "Test", "Incorrect text in comment input");
		markAsReviewedModalWindow.clearAllInputs();
		markAsReviewedModalWindow.typeComment("new test");
		markAsReviewedModalWindow.clickMarkAsReviewedButton();
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Test run #" + testRunViewTypes.get(0).getTestRunType().getId() + " marked as reviewed");
		Assert.assertTrue(testRunTableRow.isElementPresent(testRunTableRow.getCommentIcon(), 1), "Comment icon is not displayed");
		markAsReviewedModalWindow = testRunPageService.clickCommentIcon(0);
		Assert.assertEquals(markAsReviewedModalWindow.getWebElementValue(markAsReviewedModalWindow.getCommentInput()), "new test", "Incorrect text in comment input");
		markAsReviewedModalWindow.closeModalWindow();
	}

	@Test
	public void verifySendAsEmailTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		UserAPIService userAPIService = new UserAPIService();
		UserType userType = userAPIService.createUsers(1).get(0);
		testRunPage = (TestRunPage) testRunPage.reload();
		SendAsEmailModalWindow sendAsEmailModalWindow = testRunPageService.clickSendAsEmailButton(0);
		Assert.assertEquals(sendAsEmailModalWindow.getHeaderText(), "Email", "Modal is not opened");
		sendAsEmailModalWindow.typeRecipients(userType.getEmail().substring(0, 4));
		sendAsEmailModalWindow.clickSuggestion(0);
		Chip chip = sendAsEmailModalWindow.getChips().get(0);
		Assert.assertTrue(chip.isElementPresent(chip.getCloseButton(), 1), "Chip is not present");
		Assert.assertEquals(chip.getContentText(), userType.getEmail(), "Invalid email in the chip");
		chip.clickCloseButton();
		Assert.assertTrue(! sendAsEmailModalWindow.isElementPresent(chip.getRootElement(), 1), "Chip is present");
		sendAsEmailModalWindow.typeRecipients(userType.getEmail().substring(0, 4));
		sendAsEmailModalWindow.clickSuggestion(0);
		sendAsEmailModalWindow.clickSendButton();
		testRunPage.waitUntilPageIsLoaded();
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Email was successfully sent!", "Email can not send");
		sendAsEmailModalWindow = testRunPageService.clickSendAsEmailButton(0);
		sendAsEmailModalWindow.typeRecipients(userType.getEmail());
		sendAsEmailModalWindow.clickSendButton();
		Assert.assertEquals(sendAsEmailModalWindow.getSuccessAlert().getText(), "Email was successfully sent!", "Email can not send");
	}

	@Test
	public void verifyExportTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		testRunPageService.clickExportButton(0);
	}

	@Test
	public void verifyBuildNowTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		BuildNowModalWindow buildNowModalWindow = testRunPageService.clickBuildNowButton(0);
		Assert.assertEquals(buildNowModalWindow.getHeaderText(), "Build now", "Modal window title is incorrect");
		buildNowModalWindow.closeModalWindow();
	}

	@Test
	public void verifyRebuildTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		RebuildModalWindow rebuildModalWindow = testRunPageService.clickRebuildButton(0);
		Assert.assertEquals(rebuildModalWindow.getHeaderText(), "Rebuild testrun", "Modal title is incorrect");
		rebuildModalWindow.clickOnlyFailuresRadioButton();
		rebuildModalWindow.clickAllTestsRadioButton();
		rebuildModalWindow.clickCancelButton();
		Assert.assertFalse(rebuildModalWindow.isElementPresent(1));
		rebuildModalWindow = testRunPageService.clickRebuildButton(0);
		rebuildModalWindow.clickOnlyFailuresRadioButton();
		rebuildModalWindow.clickRerunButton();
	}

	@Test
	public void verifyDeleteTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		String testRunName = testRunPageService.getTestRunRowByIndex(0).getTestRunNameText();
		testRunPageService.getTestRunRowByIndex(0).clickTestRunSettingMenu().clickDeleteButton();
		Alert alert = driver.switchTo().alert();
		Assert.assertEquals(alert.getText(), "Do you really want to delete \"" + testRunName + "\" test run?");
		alert.dismiss();
		Assert.assertEquals(testRunPageService.getTestRunRowByIndex(0).getTestRunNameText(), testRunName, "Test run is deleted");
		testRunPageService.getTestRunRowByIndex(0).clickTestRunSettingMenu().clickDeleteButton();
		alert = driver.switchTo().alert();
		alert.accept();
		testRunPage.waitUntilPageIsLoaded();
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Test run #" + testRunViewTypes.get(0).getTestRunType().getId() + " removed");
		Assert.assertNotEquals(testRunPageService.getTestRunRowByIndex(0).getTestRunNameText(), testRunName, "Test run is not deleted");
	}

	@Test
	public void verifyTestRunsTable()
	{
		List<TestRun> testRuns = testRunMapper.searchTestRuns(new TestRunSearchCriteria());
		Assert.assertEquals(testRunPage.getPageItemsCount(), testRunMapper.getTestRunsSearchCount(new TestRunSearchCriteria()), "Invalid test runs count presents");
		int count = testRunPage.getTestRunTable().getTestRunTableRows().size();
		Assert.assertEquals(count, testRuns.size() <= 20 ? count : 20, "Invalid test run rows count on the page");
		int generateCount = count <= 20 ? 25 - count : 1;
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(generateCount, 1);
		testRunPage = (TestRunPage) testRunPage.reload();
		TestRun testRunView = testRunMapper.searchTestRuns(new TestRunSearchCriteria()).get(0);
		verifyTestRunInformation(testRunView, 0);
	}

	@Test
	public void verifyTestInfoTest()
	{
		int count = testRunPage.getTestRunTable().getTestRunTableRows().size();
		int generateCount = count <= 20 ? 25 - count : 1;
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(generateCount, 1);
		testRunPage = (TestRunPage) testRunPage.reload();
		TestRun testRunView = testRunMapper.searchTestRuns(new TestRunSearchCriteria()).get(0);
		verifyTestRunTestInformation(testRunView, 0);
	}

	@Test
	public void verifyTestRunSearchTest()
	{
		TestRunAPIService testRunAPIService = new TestRunAPIService();
		TestRunTypeBuilder testRunTypeBuilder = new TestRunTypeBuilder();
		TestRunViewType testRunViewType = testRunAPIService.createTestRun(testRunTypeBuilder, 2, 0, 0, 0, 0, 0);
		testRunPageService.clickMarkAsReviewedButton(0).clickMarkAsReviewedButton();
		generateTestRunsIfNeed(0, 2);
		testRunPage = (TestRunPage) testRunPage.reload();
		TestRun testRun = testRunMapper.searchTestRuns(new TestRunSearchCriteria() {
			{
				setId(testRunViewType.getTestRunType().getId());
			}
		}).get(0);
		testRunPageService.search("PASSED", null, null, null, false, null, null);
		verifyTestRunInformation(testRun, 0);
		testRunPageService.clearSearchForm();
		testRunPageService.search(null, testRun.getTestSuite().getName().split(" ")[testRun.getTestSuite().getName().split(" ").length - 1], null, null, false, null, null);
		verifyTestRunInformation(testRun, 0);
		testRunPageService.clearSearchForm();
		testRunPageService.search(null, null, testRun.getJob().getJobURL(), null, false, null, null);
		verifyTestRunInformation(testRun, 0);
		testRunPageService.clearSearchForm();
		testRunPageService.search("PASSED", null, null, "DEMO", false, null, null);
		verifyTestRunInformation(testRun, 0);
		testRunPageService.clearSearchForm();
		testRunPageService.search(null, null, null, null, true, null, null);
		verifyTestRunInformation(testRun, 0);
		testRunPageService.clearSearchForm();
		testRunPageService.search("PASSED", null, null, null, false, "chrome", null);
		verifyTestRunInformation(testRun, 0);
		testRunPageService.clearSearchForm();
		testRunPageService.search("PASSED", null, null, null, false, null, testRun.getAppVersion());
		verifyTestRunInformation(testRun, 0);
		testRunPageService.clearSearchForm();
	}

	private void verifyTestRunInformation(TestRun testRun, int index)
	{
		TestRunTableRow testRunTableRow = testRunPageService.getTestRunRowByIndex(index);
		Assert.assertTrue(testRunTableRow.getCheckbox().isDisplayed(), "Checkbox is not displayed");
		Assert.assertEquals(testRunTableRow.getTestRunNameText(), testRun.getTestSuite().getName(), "Invalid test suite name");
		Assert.assertEquals(testRunTableRow.getTestSuiteFileName(), testRun.getTestSuite().getFileName(), "Invalid test suite file name");
		Assert.assertEquals(testRunTableRow.getAppVersionText(), testRun.getAppVersion(), "Invalid test run app version");
		Assert.assertEquals(testRunTableRow.getEnvironmentText(), testRun.getEnv(), "Invalid test run environment");
		Assert.assertEquals(testRunTableRow.getPlatform(), testRun.getPlatform().toLowerCase(), "Invalid platform");
		Assert.assertEquals(testRunTableRow.getPassedCount(), testRun.getPassed(), "Invalid passed tests count");
		Assert.assertEquals(testRunTableRow.getFailedCount(), testRun.getFailed(), "Invalid failed tests count");
		Assert.assertEquals(testRunTableRow.getKnownIssuesCount(), testRun.getFailedAsKnown(), "Invalid known issues count");
		Assert.assertEquals(testRunTableRow.getBlockersCount(), testRun.getFailedAsBlocker(), "Invalid tests blockers count");
		Assert.assertEquals(testRunTableRow.getSkippedCount(), testRun.getSkipped(), "Invalid skipped tests count");
		Assert.assertEquals(testRunTableRow.getInProgressCount(), testRun.getInProgress(), "Invalid in progress count");
		testRunTableRow.hoverOnElement(testRunTableRow.getEnvironment());
		Assert.assertTrue(testRunTableRow.getExpandTestsIcon().isDisplayed(), "Expand icon is not present on hover");
		TestTable testTable = testRunTableRow.clickExpandTestsIcon();
		Assert.assertEquals(testTable.getTestRows().size(), testMapper.searchTests(new TestSearchCriteria() {
			{
				setTestRunId(testRun.getId());
			}
		}).size());
		testRunTableRow.hoverOnElement(testRunTableRow.getEnvironment());
		testTable = testRunTableRow.clickExpandTestsIcon();
		Assert.assertFalse(testTable.isElementPresent(1), "Test table is visible after closing");
	}

	private void verifyTestRunTestInformation(TestRun testRun, int index)
	{
		TestRunTableRow testRunTableRow = testRunPageService.getTestRunRowByIndex(index);
		List<com.qaprosoft.zafira.models.db.Test> tests = testMapper.searchTests(new TestSearchCriteria() {
			{
				setTestRunId(testRun.getId());
			}
		});
		testRunTableRow.hoverOnElement(testRunTableRow.getEnvironment());
		TestTable testTable = testRunTableRow.clickExpandTestsIcon();
		Assert.assertEquals(tests.size(), testRunTableRow.getTestTable().getTestRows().size(), "Invalid tests count visible");
		IntStream.iterate(0, i -> i++).limit(testTable.getTestRows().size()).forEach(i -> {
			com.qaprosoft.zafira.models.db.Test currentTest = tests.get(i);
			TestRow currentTestRow = testTable.getTestRows().get(i);
			Status status = currentTestRow.getStatus();
			Assert.assertEquals(currentTest.getStatus(), status, "Incorrect test status visible");
			Assert.assertEquals(currentTest.getName(), currentTestRow.getTestNameText(), "Invalid test name text");
			Assert.assertEquals(currentTest.getOwner(), currentTestRow.getOwnerName(), "Invalid owner");
			//Assert.assertEquals(currentTest.getTestConfig().getDevice(), currentTestRow.getDeviceName(), "Incorrect device");
			Assert.assertEquals(currentTest.getWorkItem(WorkItem.Type.TASK).getJiraId(), currentTestRow.getTaskTicket(), "Incorrect work item id");
			boolean isShowMoreLinkPresent = currentTestRow.isElementPresent(currentTestRow.getShowMoreLink(), 1);
			switch(status)
			{
				case ABORTED:
					Assert.assertTrue(currentTest.getMessage().length() > 100 == isShowMoreLinkPresent, "Show more link is not present");
					Assert.assertTrue(currentTest.getMessage().length() > 100 == (currentTestRow.getShowMoreLogText().length() == 100), "Show more link is not present");
					Assert.assertTrue(currentTest.getMessage().contains(currentTestRow.getShowLessLogText()), "Show more visible incorrect");
				case PASSED:
					Assert.assertFalse(currentTestRow.isElementPresent(currentTestRow.getMarkAsPassed(), 1), "Mark as passed is visible");
					Assert.assertFalse(currentTestRow.isElementPresent(currentTestRow.getMarkAsKnownIssue(), 1), "Mark as known issue is visible");
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getEditTask(), 1), "Edit task is not visible");
					break;
				case FAILED:
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getMarkAsPassed(), 1), "Mark as passed is not visible");
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getMarkAsKnownIssue(), 1), "Mark as known issue is not visible");
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getEditTask(), 1), "Edit task is not visible");

					Assert.assertTrue(currentTest.getMessage().length() > 100 == isShowMoreLinkPresent, "Show more link is not present");
					Assert.assertTrue(currentTest.getMessage().length() > 100 == (currentTestRow.getShowMoreLogText().length() == 100), "Show more link is not present");
					Assert.assertTrue(currentTest.getMessage().contains(currentTestRow.getShowLessLogText()), "Show more visible incorrect");
					break;
				case SKIPPED:
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getMarkAsPassed(), 1), "Mark as passed is not visible");
					Assert.assertFalse(currentTestRow.isElementPresent(currentTestRow.getMarkAsKnownIssue(), 1), "Mark as known issue is visible");
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getEditTask(), 1), "Edit task is not visible");
					break;
				case IN_PROGRESS:
					Assert.assertFalse(currentTestRow.isElementPresent(currentTestRow.getMarkAsPassed(), 1), "Mark as passed is visible");
					Assert.assertFalse(currentTestRow.isElementPresent(currentTestRow.getMarkAsKnownIssue(), 1), "Mark as known issue is visible");
					Assert.assertFalse(currentTestRow.isElementPresent(currentTestRow.getEditTask(), 1), "Edit task is visible");
					break;
			}
		});
	}

	private List<TestRunViewType> generateTestRunsIfNeed(Integer searchCount, int count)
	{
		TestRunAPIService testRunAPIService = new TestRunAPIService();
		int currentCount = searchCount == null ? testRunPage.getPageItemsCount() : searchCount;
		return testRunAPIService.createTestRuns(currentCount < count ? count - currentCount : 1,
				2, 2, 2, 2, 2, 101);
	}
}
