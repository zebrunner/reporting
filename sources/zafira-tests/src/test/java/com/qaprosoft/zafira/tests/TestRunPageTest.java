package com.qaprosoft.zafira.tests;

import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mysql.jdbc.StringUtils;
import com.qaprosoft.zafira.dbaccess.dao.mysql.TestMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.TestRunMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.UserMapper;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestSearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.WorkItem;
import com.qaprosoft.zafira.tests.gui.components.Chip;
import com.qaprosoft.zafira.tests.gui.components.FilterBlock;
import com.qaprosoft.zafira.tests.gui.components.menus.TestRunSettingMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.BuildNowModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.MarkAsReviewedModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.RebuildModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.SendAsEmailModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.TestDetailsModalWindow;
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

public class TestRunPageTest extends AbstractTest
{

	private TestRunPageService testRunPageService;
	private TestRunPage testRunPage;

	private static final String DELETE_RUNS_ALERT_TEXT = "Do you really want to delete multiple test runs?";

	@Autowired
	private TestRunMapper testRunMapper;

	@Autowired
	private TestMapper testMapper;

	@Autowired
	private UserMapper userMapper;

	@BeforeMethod
	public void setup()
	{
		testRunPageService = new TestRunPageService(driver);
		LoginPage loginPage = new LoginPage(driver);
		LoginPageService loginPageService = new LoginPageService(driver);
		loginPage.open();
		DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		dashboardPage.waitUntilPageIsLoaded();
		testRunPage = dashboardPage.getNavbar().goToTestRunPage();
		testRunPage.waitUntilPageIsLoaded();
	}

	@Test(groups = {"acceptance", "testRun", "navigation"})
	public void verifyNavigationTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage.reload();
		Assert.assertTrue(testRunPage.getPageTitleText().contains("Test runs"), "Incorrect title");
		Assert.assertNotNull(testRunPage.getPageItemsCount(), "Incorrect title");
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
		//Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getSendAsEmailButton(), 1), "Send as email button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getExportButton(), 1), "Export button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getBuildNowButton(), 1), "Build button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getRebuildButton(), 1), "Rebuild button is not visible");
		Assert.assertTrue(testRunSettingMenu.isElementPresent(testRunSettingMenu.getDeleteButton(), 1), "Delete button is not visible");
		testRunPage.clickOutside();
		pause(0.5);
		testRunPage.getTestRunSearchBlock().checkMainCheckbox();
		testRunPage.getTestRunTable().getTestRunTableRows().forEach(row -> Assert.assertTrue(row.isChecked(row.getCheckbox()), "Some checkboxes are not checked"));
	}

	@Test(groups = {"acceptance", "testRun"})
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

	@Test(groups = {"acceptance", "testRun"})
	public void verifyTestRunCopyLinkTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		TestRunTableRow testRunTableRow = testRunPageService.getTestRunRowByIndex(0);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunSettingMenu.clickCopyLinkButton();
		testRunPage.getTestRunSearchBlock().getAppVersionInput().sendKeys(Keys.CONTROL + "v");
		String url = testRunPage.getWebElementValue(testRunPage.getTestRunSearchBlock().getAppVersionInput());
		LOGGER.debug("Coped url is " + url);
		String[] urlSplit = url.split("/");
		Assert.assertEquals(urlSplit[urlSplit.length - 1], String.valueOf(testRunViewTypes.get(0).getTestRunType().getId()), "Invalid test run was opened. "
				+ "Current url: " + url + ", but test run id: " + testRunViewTypes.get(0).getTestRunType().getId());
	}

	@Test(groups = {"acceptance", "testRun"})
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
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Test run #" + testRunViewTypes.get(testRunViewTypes.size() - 1).getTestRunType().getId() + " marked as reviewed");
		pause(1);
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
		testRunPage.waitUntilElementWithTextIsPresent(testRunPage.getSuccessAlert(), "Test run #" +
				testRunViewTypes.get(testRunViewTypes.size() - 1).getTestRunType().getId() + " marked as reviewed", 5);
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Test run #" +
				testRunViewTypes.get(testRunViewTypes.size() - 1).getTestRunType().getId() + " marked as reviewed");
		Assert.assertTrue(testRunTableRow.isElementPresent(testRunTableRow.getCommentIcon(), 1), "Comment icon is not displayed");
		markAsReviewedModalWindow = testRunPageService.clickCommentIcon(0);
		Assert.assertEquals(markAsReviewedModalWindow.getWebElementValue(markAsReviewedModalWindow.getCommentInput()), "new test", "Incorrect text in comment input");
		markAsReviewedModalWindow.closeModalWindow();
	}

	@Test(groups = {"acceptance", "testRun"})
	public void verifySendAsEmailTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		String email = userMapper.getUserSearchCount(new UserSearchCriteria()) > 2 ? userMapper.searchUsers(new UserSearchCriteria()
		{
			{
				setId(3L);
			}
		}).get(0).getEmail() : new UserAPIService().createUsers(2).get(0).getEmail();
		testRunPage = (TestRunPage) testRunPage.reload();
		SendAsEmailModalWindow sendAsEmailModalWindow = testRunPageService.clickSendAsEmailButton(0);
		Assert.assertEquals(sendAsEmailModalWindow.getHeaderText(), "Email", "Modal is not opened");
		pause(2);
		sendAsEmailModalWindow.typeRecipients(email.substring(0, 5));
		sendAsEmailModalWindow.waitUntilElementIsNotPresent(sendAsEmailModalWindow.getProgressLinear(), 2);
		pause(0.5);
		sendAsEmailModalWindow.clickSuggestion(0);
		Chip chip = sendAsEmailModalWindow.getChips().get(0);
		Assert.assertTrue(chip.isElementPresent(chip.getCloseButton(), 1), "Chip is not present");
		Assert.assertEquals(chip.getContentText(true), email, "Invalid email in the chip. Current email text is: " + chip.getContentText(true));
		chip.clickCloseButton();
		Assert.assertTrue(! sendAsEmailModalWindow.isElementPresent(chip.getRootElement(), 1), "Chip is present");
		pause(2);
		sendAsEmailModalWindow.typeRecipients(email.substring(0, 5));
		sendAsEmailModalWindow.clickSuggestion(0);
		sendAsEmailModalWindow.clickSendButton();
		testRunPage.waitUntilPageIsLoaded();
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Email was successfully sent!", "Email can not send");
	}

	@Test(groups = {"acceptance", "testRun"})
	public void verifyExportTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		testRunPageService.clickExportButton(0);
	}

	@Test(groups = {"acceptance", "testRun"})
	public void verifyBuildNowTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		BuildNowModalWindow buildNowModalWindow = testRunPageService.clickBuildNowButton(0);
		Assert.assertEquals(buildNowModalWindow.getHeaderText(), "Build now", "Modal window title is incorrect");
		buildNowModalWindow.closeModalWindow();
	}

	@Test(groups = {"acceptance", "testRun"})
	public void verifyRebuildTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		RebuildModalWindow rebuildModalWindow = testRunPageService.clickRebuildButton(0);
		Assert.assertEquals(rebuildModalWindow.getHeaderText(), "Rebuild testrun", "Modal title is incorrect");
		rebuildModalWindow.clickOnlyFailuresRadioButton();
		rebuildModalWindow.clickAllTestsRadioButton();
		rebuildModalWindow.clickCancelButton();
		pause(2);
		testRunPage.clickOutside();
		pause(1);
		Assert.assertFalse(rebuildModalWindow.isElementPresent(1), "Rebuild modal window is present");
		rebuildModalWindow = testRunPageService.clickRebuildButton(0);
		rebuildModalWindow.clickOnlyFailuresRadioButton();
		rebuildModalWindow.clickRerunButton();
	}

	@Test(groups = {"acceptance", "testRun"})
	public void verifyDeleteTest()
	{
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 25);
		testRunPage = (TestRunPage) testRunPage.reload();
		String testRunName = testRunPageService.getTestRunRowByIndex(0).getTestRunNameText();
		testRunPageService.getTestRunRowByIndex(0).clickTestRunSettingMenu().clickDeleteButton();
		Alert alert = driver.switchTo().alert();
		Assert.assertEquals(alert.getText(), "Do you really want to delete \"" + testRunName + "\" test run?");
		alert.dismiss();
		testRunPage.clickOutside();
		pause(1);
		Assert.assertEquals(testRunPageService.getTestRunRowByIndex(0).getTestRunNameText(), testRunName, "Test run is deleted");
		TestRunSettingMenu testRunSettingMenu = testRunPageService.getTestRunRowByIndex(0).clickTestRunSettingMenu();
		testRunSettingMenu.clickDeleteButton();
		alert = testRunPage.getAlert();
		alert.accept();
		testRunPage.waitUntilPageIsLoaded();
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Test run #" + testRunViewTypes.get(0).getTestRunType().getId() + " removed");
		Assert.assertNotEquals(testRunPageService.getTestRunRowByIndex(0).getTestRunNameText(), testRunName, "Test run is not deleted");
	}

	@Test(groups = {"acceptance", "testRun"})
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

	@Test(groups = {"acceptance", "testRun"})
	public void verifyTestInfoTest()
	{
		int count = testRunPage.getTestRunTable().getTestRunTableRows().size();
		int generateCount = count <= 20 ? 25 - count : 1;
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(generateCount, 1);
		testRunPage = (TestRunPage) testRunPage.reload();
		TestRun testRunView = testRunMapper.searchTestRuns(new TestRunSearchCriteria()).get(0);
		verifyTestRunTestInformation(testRunView, 0);
	}

	@Test(groups = {"acceptance", "testRun", "search"})
	public void verifyTestRunSearchTest()
	{
		TestRunAPIService testRunAPIService = new TestRunAPIService();
		TestRunTypeBuilder testRunTypeBuilder = new TestRunTypeBuilder();
		TestRunViewType testRunViewType = testRunAPIService.createTestRun(testRunTypeBuilder, 2, 0, 0, 0, 0, 0);
		testRunPage = (TestRunPage) testRunPage.reload();
		testRunPageService.clickMarkAsReviewedButton(0).clickMarkAsReviewedButton();
		generateTestRunsIfNeed(0, 2);
		TestRunSearchCriteria sc = new TestRunSearchCriteria() {
			{
				setId(testRunViewType.getTestRunType().getId());
			}
		};
		TestRun testRun = testRunMapper.searchTestRuns(sc).get(0);
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
		verifyTestRunInformation(testRun, 2);
		testRunPageService.search("PASSED", null, null, null, false, "chrome", null);
		verifyTestRunInformation(testRun, 0);
		testRunPageService.clearSearchForm();
		testRunPageService.search("PASSED", null, null, null, false, null, testRun.getAppVersion());
		verifyTestRunInformation(testRun, 0);
		testRunPageService.clearSearchForm();
	}

	@Test(groups = {"acceptance", "testRun", "search"})
	public void verifyPaginationTest()
	{
		Assert.assertTrue(testRunPage.hasDisabledAttribute(testRunPage.getPaginationBlock().getFirstPageButton()), "First page button is not disabled");
		Assert.assertTrue(testRunPage.hasDisabledAttribute(testRunPage.getPaginationBlock().getPreviousPageButton()), "Previous page button is not disabled");
		if(testRunPage.getPageItemsCount() > 20)
		{
			Assert.assertFalse(testRunPage.hasDisabledAttribute(testRunPage.getPaginationBlock().getNextPageButton()),
					"Next page button is not disabled");
			Assert.assertFalse(testRunPage.hasDisabledAttribute(testRunPage.getPaginationBlock().getLastPageButton()),
					"Last page button is not disabled");
		} else
		{
			Assert.assertTrue(testRunPage.hasDisabledAttribute(testRunPage.getPaginationBlock().getNextPageButton()),
					"Next page button is not disabled");
			Assert.assertTrue(testRunPage.hasDisabledAttribute(testRunPage.getPaginationBlock().getLastPageButton()),
					"Last page button is not disabled");
		}
		List<TestRunViewType> testRunViewTypes = generateTestRunsIfNeed(testRunPage.getPageItemsCount(), 60);
		testRunPage.reload();
		testRunPage.waitUntilPageIsLoaded();
		int totalCount = testRunMapper.getTestRunsSearchCount(new TestRunSearchCriteria());
		Assert.assertEquals(testRunPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, 1, 20, totalCount), "Count of user menu buttons is not 20");
		testRunPageService.goToNextPage();
		Assert.assertEquals(testRunPageService.getTestRunTableRowsCount(), 20, "Count of user menu buttons is not 20");
		Assert.assertEquals(testRunPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, 21, 40, totalCount), "Count of user menu buttons is not 20");
		testRunPageService.goToPreviousPage();
		Assert.assertEquals(testRunPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, 1, 20, totalCount), "Count of user menu buttons is not 20");
		testRunPageService.goToLastPage();
		int decriment = totalCount % 20 == 0 ? 20 : totalCount % 20;
		Assert.assertEquals(testRunPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, totalCount - decriment + 1, totalCount, totalCount), "Count of user menu buttons is not 20");
		testRunPageService.goToFirstPage();
		Assert.assertEquals(testRunPage.getPaginationBlock().getCountOfPageElementsText(), String.format(COUNT_OF_PAGE_ELEMENTS, 1, 20, totalCount), "Count of user menu buttons is not 20");
	}

	@Test(groups = {"acceptance", "testRun"})
	public void verifyFabButtonActionsTest()
	{
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getFabButton().getRootElement(), 1), "Main fab button is present");
		testRunPageService.getTestRunRowByIndex(0).checkCheckbox();
		testRunPageService.getTestRunRowByIndex(1).checkCheckbox();
		Assert.assertFalse(testRunPage.isChecked(testRunPage.getTestRunSearchBlock().getMainCheckbox()), "Main checkbox is checked");
		testRunPage.getTestRunSearchBlock().checkMainCheckbox();
		testRunPage.getTestRunTable().getTestRunTableRows().forEach(row -> Assert.assertTrue(testRunPage.isChecked(row.getCheckbox()), "Test run row checkbox is not checked"));
		testRunPage.getTestRunSearchBlock().uncheckMainCheckbox();
		testRunPageService.getTestRunRowByIndex(0).checkCheckbox();
		testRunPageService.getTestRunRowByIndex(1).checkCheckbox();
		testRunPage.getFabButton().clickButtonTrigger();
		testRunPage.getFabButton().clickButtonMiniByClassName("trash");
		Assert.assertEquals(testRunPage.getAlert().getText(), DELETE_RUNS_ALERT_TEXT, "Incorrect title of delete runs alert");
		testRunPage.getAlert().dismiss();
		List<TestRun> testRuns = testRunMapper.searchTestRuns(new TestRunSearchCriteria());
		Assert.assertEquals(testRunPageService.getTestRunRowByIndex(0).getTestRunNameText(), testRuns.get(0).getTestSuite().getName(), "Test run is not deleted");
		Assert.assertEquals(testRunPageService.getTestRunRowByIndex(1).getTestRunNameText(), testRuns.get(1).getTestSuite().getName(), "Test run is not deleted");
		testRunPage = (TestRunPage) testRunPage.reload();
		testRunPageService.getTestRunRowByIndex(0).checkCheckbox();
		testRunPageService.getTestRunRowByIndex(1).checkCheckbox();
		testRunPage.getFabButton().clickButtonTrigger();
		testRunPage.getFabButton().clickButtonMiniByClassName("trash");
		pause(2);
		testRunPage.getAlert().accept();
		testRunPage.waitUntilPageIsLoaded();
		Assert.assertNotEquals(testRunPageService.getTestRunRowByIndex(0).getTestRunNameText(), testRuns.get(0).getTestSuite().getName(), "Test run is not deleted");
		Assert.assertNotEquals(testRunPageService.getTestRunRowByIndex(1).getTestRunNameText(), testRuns.get(1).getTestSuite().getName(), "Test run is not deleted");
	}

	@Test(groups = {"acceptance", "testRun"})
	public void verifyTestDetailsModalTest()
	{
		TestRunAPIService testRunAPIService = new TestRunAPIService();
		TestRunTypeBuilder testRunTypeBuilder = new TestRunTypeBuilder();
		testRunAPIService.createTestRun(testRunTypeBuilder,  0, 1, 0, 0, 0, 200);
		TestTable testTable = testRunPageService.getTestTableByRowIndex(0);
		TestRow testRow = testTable.getTestRows().get(0);
		Assert.assertTrue(testTable.isElementPresent(testRow.getOpenTestDetailsModalButton(), 1), "Details modal button is not present");
		TestDetailsModalWindow testDetailsModalWindow = testRow.clickOpenTestDetailsModalButton();
		Assert.assertEquals(testDetailsModalWindow.getHeaderText(), "Test details", "Details modal is not opened");
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getStatusTabHeading(), 1), "Status tab is not opened");
		Assert.assertTrue(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getSaveButton()), "Save button is not disabled");
		Assert.assertFalse(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getChangeStatusSelect(), 1), "Change status select is visible");
		testDetailsModalWindow.clickEditButton();
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getChangeStatusSelect(), 1), "Change status select is not visible");
        String currentStatus = testDetailsModalWindow.getChangeStatusSelect().getText();
		Assert.assertFalse(StringUtils.isEmptyOrWhitespaceOnly(currentStatus), "No status is displayed in select");
		testDetailsModalWindow.select(testDetailsModalWindow.getChangeStatusSelect(),  currentStatus);
		Assert.assertTrue(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getSaveButton()), "Save button is not disabled");
		pause(0.2);
		testDetailsModalWindow.select(testDetailsModalWindow.getChangeStatusSelect(),  currentStatus.equals("PASSED") ? "FAILED" : "PASSED");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getSaveButton()), "Save button is disabled");
		testDetailsModalWindow.clickSaveButton();
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getSuccessAlert(),2));

		/* ISSUES */

		testDetailsModalWindow.clickIssuesTab();
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getIssuesTabHeading(), 1), "Issue tab is not opened");
		Assert.assertEquals(testDetailsModalWindow.getIssueInput().getAttribute("placeholder"), "Not connected to JIRA", "Incorrect placeholder");
		testDetailsModalWindow.typeIssueJiraId("JIRA-2222");
		Assert.assertTrue(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getAssignIssueButton()), "Assign button is not disabled");
		testDetailsModalWindow.clearAllModalInputs();
		testDetailsModalWindow.typeIssueDescription("description");
		Assert.assertTrue(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getAssignIssueButton()), "Assign button is not disabled");
		testDetailsModalWindow.typeIssueJiraId("JIRA-2222");
		testDetailsModalWindow.typeIssueDescription("description");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getAssignIssueButton()), "Assign button is disabled");
		testDetailsModalWindow.clickAssignIssueButton();
		testRunPage.waitUntilPageIsLoaded();
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getSuccessAlert(),2), "Success alert is not present");
		testDetailsModalWindow.waitUntilElementIsNotPresent(testDetailsModalWindow.getSuccessAlert(),3);
		Assert.assertEquals(testRow.getKnownIssueTicket(), "JIRA-2222", "Invalid known issue label text");
        Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getIssueInput()), "JIRA-2222", "Incorrect jira id in input");
		Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getIssueTextArea()), "description", "Incorrect jira description in input");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getUnassignIssueButton()), "Unassign button is disabled");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getUpdateIssueButton()), "Update button is disabled");
		testDetailsModalWindow.clearAllModalInputs();
		testDetailsModalWindow.typeIssueJiraId("JIRA-6666");
		testDetailsModalWindow.typeIssueDescription("new description");
		testDetailsModalWindow.clickAssignIssueButton();
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getSuccessAlert(),2), "Success alert is not present");
		Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getIssueInput()), "JIRA-6666", "Incorrect jira id in input");
		Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getIssueTextArea()), "new description", "Incorrect jira description in input");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getUnassignIssueButton()), "Unassign button is disabled");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getUpdateIssueButton()), "Update button is disabled");
		testDetailsModalWindow.clickUnassignIssueButton();
		testDetailsModalWindow.getAlert().accept();
		Assert.assertTrue(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getIssueInput()).isEmpty(), "Jira id input is not empty");
		Assert.assertTrue(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getIssueTextArea()).isEmpty(), "Description input is not empty");
		testDetailsModalWindow.clickIssuesListControl();
		testDetailsModalWindow.getIssuesListItems().get(0).click();
		Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getIssueInput()), "JIRA-2222", "Incorrect jira id in input");
		testDetailsModalWindow.checkBlockerCheckbox();
		testDetailsModalWindow.clickUpdateIssueButton();
		testDetailsModalWindow.closeModalWindow();
		testRunPage.waitUntilElementIsNotPresent(testDetailsModalWindow.getRootElement(), 2);
		Assert.assertTrue(testRow.isElementPresent(testRow.getBlockerLabel(), 5), "Blocker label is not present");
		testRunPage.waitUntilElementIsNotPresent(testRow.getOpenTestDetailsModalButton(),1);
		testDetailsModalWindow = testRow.clickOpenTestDetailsModalButton();
		testDetailsModalWindow.clickIssuesTab();
		testDetailsModalWindow.waitUntilElementIsPresent(testDetailsModalWindow.getUnassignIssueButton(),1);
		testDetailsModalWindow.clickUnassignIssueButton();
		pause(0.2);
		testDetailsModalWindow.getAlert().accept();
		testDetailsModalWindow.closeModalWindow();
		Assert.assertFalse(testRow.isElementPresent(testRow.getBlockerLabel(), 5), "Blocker label is present");

		/* TASKS */

		testDetailsModalWindow = testRow.clickOpenTestDetailsModalButton();
		testDetailsModalWindow.waitUntilElementIsNotPresent(testDetailsModalWindow.getProgressLinear(),5);
		testDetailsModalWindow.clickTasksTab();
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getTasksTabHeading(), 1), "Task tab is not opened");
		testDetailsModalWindow.clearAllModalInputs();
		Assert.assertEquals(testDetailsModalWindow.getTaskInput().getAttribute("placeholder"), "Not connected to JIRA", "Incorrect placeholder");
		testDetailsModalWindow.typeTaskJiraId("JIRA-2222");
		Assert.assertTrue(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getAssignTaskButton()), "Assign button is not disabled");
		testDetailsModalWindow.clearAllModalInputs();
		testDetailsModalWindow.typeTaskDescription("description");
		Assert.assertTrue(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getAssignTaskButton()), "Assign button is not disabled");
		testDetailsModalWindow.typeTaskJiraId("JIRA-2222");
		testDetailsModalWindow.typeTaskDescription("description");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getAssignTaskButton()), "Assign button is disabled");
		testDetailsModalWindow.clickAssignTaskButton();
		testRunPage.waitUntilPageIsLoaded();
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getSuccessAlert(),1), "Success alert is not present");
		testDetailsModalWindow.waitUntilElementIsNotPresent(testDetailsModalWindow.getSuccessAlert(),3);
		Assert.assertEquals(testRow.getTaskTicket(), "JIRA-2222", "Invalid known Task label text");
		Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getTaskInput()), "JIRA-2222", "Incorrect jira id in input");
		Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getTaskTextArea()), "description", "Incorrect jira description in input");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getUnassignTaskButton()), "Unassign button is disabled");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getUpdateTaskButton()), "Update button is disabled");
		testDetailsModalWindow.clearAllModalInputs();
		testDetailsModalWindow.typeTaskJiraId("JIRA-6666");
		testDetailsModalWindow.typeTaskDescription("new description");
		testDetailsModalWindow.clickAssignTaskButton();
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getSuccessAlert(),2), "Success alert is not present");
		Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getTaskInput()), "JIRA-6666", "Incorrect jira id in input");
		Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getTaskTextArea()), "new description", "Incorrect jira description in input");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getUnassignTaskButton()), "Unassign button is disabled");
		Assert.assertFalse(testDetailsModalWindow.hasDisabledAttribute(testDetailsModalWindow.getUpdateTaskButton()), "Update button is disabled");
		testDetailsModalWindow.clickUnassignTaskButton();
		testDetailsModalWindow.waitUntilElementIsNotPresent(testDetailsModalWindow.getSuccessAlert(),2);
		testDetailsModalWindow.getAlert().accept();
		Assert.assertTrue(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getTaskInput()).isEmpty(), "Jira id input is not empty");
		Assert.assertTrue(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getTaskTextArea()).isEmpty(), "Description input is not empty");
		testDetailsModalWindow.clickTasksListControl();
		testDetailsModalWindow.getTaskListItems().get(0).click();
		Assert.assertEquals(testDetailsModalWindow.getWebElementValue(testDetailsModalWindow.getTaskInput()), "JIRA-2222", "Incorrect jira id in input");
		testDetailsModalWindow.clickUpdateTaskButton();
		testDetailsModalWindow.clickUnassignTaskButton();
		testDetailsModalWindow.getAlert().accept();

		/* COMMENTS */

		testDetailsModalWindow.clickCommentsTab();
		int startCommentsCount = parseCommentsCount(testDetailsModalWindow.getCommentsTabHeading().getText());
		Assert.assertTrue(testDetailsModalWindow.isElementPresent(testDetailsModalWindow.getCommentTextArea(), 1), "Textarea is not visible");
		testDetailsModalWindow.typeComment("Text");
		testDetailsModalWindow.clickAddCommentButton();
		int afterCommentActionCommentsCount = parseCommentsCount(testDetailsModalWindow.getCommentsTabHeading().getText());
		Assert.assertTrue((afterCommentActionCommentsCount - startCommentsCount) == 1);
		testDetailsModalWindow.closeModalWindow();

	}

	/*
			Filters
	 */

	@Test(groups = {"acceptance", "testRun"})
	public void verifyFiltersTest()
	{
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getCreateFilterButton(), 2), "Filter create button is present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getSaveFilterButton(), 2), "Filter save button is present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getCancelFilterButton(), 2), "Filter cancel button is present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getDeleteFilterButton(), 2), "Filter delete button is present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getTestRunSearchBlock().getSearchButton(), 2), "Test runs search button is present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getTestRunSearchBlock().getClearButton(), 2), "Test runs clear button is present");

		FilterBlock filterBlock = testRunPage.getFilterBlock();
		filterBlock.clickManageChip();
		Assert.assertTrue(testRunPage.isElementPresent(testRunPage.getCreateFilterButton(), 2), "Filter create button is not present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getSaveFilterButton(), 2), "Filter save button is present");
		Assert.assertTrue(testRunPage.isElementPresent(testRunPage.getCancelFilterButton(), 2), "Filter cancel button is not present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getDeleteFilterButton(), 2), "Filter delete button is present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getTestRunSearchBlock().getSearchButton(), 2), "Test runs search button is present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getTestRunSearchBlock().getClearButton(), 2), "Test runs clear button is present");
		Assert.assertTrue(testRunPage.hasDisabledAttribute(testRunPage.getCreateFilterButton()), "Test runs clear button is not disabled");

		testRunPage.clickCancelFilterButton();
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getFilterBlock().getFilterEditorBlock().getRootElement(), 2), "Create filter block is present");

		filterBlock.clickManageChip();
		String generatedName = "test" + RandomUtils.nextInt(0, 10000);
		filterBlock.getFilterEditorBlock().typeFilterName(generatedName);
		filterBlock.getFilterEditorBlock().typeFilterDescription("test");
		Assert.assertFalse(testRunPage.hasDisabledAttribute(testRunPage.getCreateFilterButton()), "Test runs clear button is not disabled");
		filterBlock.getFilterEditorBlock().createCriterias("Job Url:Equals:testURL", "Env:Not Equals:demo");
		testRunPage.clickCreateFilterButton();
		testRunPage.waitUntilAlertWithTextIsPresent(5);
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Filter was created", "Filter creation success alert is not present");
		Chip neededChip = filterBlock.getFilterCreatedChipByName(generatedName);
		Assert.assertEquals(neededChip.getContentText(false).toLowerCase(), generatedName, "Chip has invalid text");

		testRunPage.hoverOnElement(neededChip.getRootElement());
		Assert.assertTrue(filterBlock.isElementPresent(neededChip.getSupportIcon(), 2), "Edit icon is not present");

		neededChip.clickSupportIcon();
		pause(2);
		Assert.assertEquals(filterBlock.getWebElementValue(filterBlock.getFilterEditorBlock().getFilterNameInput()), generatedName, "Filter name is not correct");
		Assert.assertEquals(filterBlock.getWebElementValue(filterBlock.getFilterEditorBlock().getFilterDescriptionInput()), "test", "Filter description is not correct");
		Assert.assertEquals(filterBlock.getFilterEditorBlock().getFilterChipScope().get(0).getContentText(false), "Job Url == 'testurl'", "Invalid chip with index 0");
		Assert.assertEquals(filterBlock.getFilterEditorBlock().getFilterChipScope().get(1).getContentText(false).toLowerCase(), "env != 'demo'", "Invalid chip with index 1");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getCreateFilterButton(), 2), "Filter create button is not present");
		Assert.assertTrue(testRunPage.isElementPresent(testRunPage.getSaveFilterButton(), 2), "Filter save button is present");
		Assert.assertTrue(testRunPage.isElementPresent(testRunPage.getCancelFilterButton(), 2), "Filter cancel button is not present");
		Assert.assertTrue(testRunPage.isElementPresent(testRunPage.getDeleteFilterButton(), 2), "Filter delete button is present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getTestRunSearchBlock().getSearchButton(), 2), "Test runs search button is present");
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getTestRunSearchBlock().getClearButton(), 2), "Test runs clear button is present");

		testRunPage.clickCancelFilterButton();
		Assert.assertFalse(testRunPage.isElementPresent(testRunPage.getFilterBlock().getFilterEditorBlock().getRootElement(), 2), "Create filter block is present");

		testRunPage.hoverOnElement(neededChip.getRootElement());
		neededChip.clickSupportIcon();

		generatedName = "test" + RandomUtils.nextInt(0, 10000);
		filterBlock.getFilterEditorBlock().typeFilterName(generatedName);
		filterBlock.getFilterEditorBlock().typeFilterDescription("test1");
		filterBlock.getFilterEditorBlock().getFilterChipScope().get(0).clickCloseButton();
		testRunPage.clickSaveFilterButton();
		testRunPage.waitUntilAlertWithTextIsPresent(5);
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Filter was updated", "Incorrect success alert text");

		neededChip = filterBlock.getFilterCreatedChipByName(generatedName);

		testRunPage.hoverOnElement(neededChip.getRootElement());
		neededChip.clickSupportIcon();
		pause(0.2);
		Assert.assertEquals(filterBlock.getWebElementValue(filterBlock.getFilterEditorBlock().getFilterNameInput()), generatedName, "Filter name is not correct");
		Assert.assertEquals(filterBlock.getWebElementValue(filterBlock.getFilterEditorBlock().getFilterDescriptionInput()), "test1", "Filter description is not correct");
		Assert.assertEquals(filterBlock.getFilterEditorBlock().getFilterChipScope().get(0).getContentText(false), "Env != 'DEMO'", "Invalid chip with index 0");
		Assert.assertEquals(filterBlock.getFilterEditorBlock().getFilterChipScope().size(), 1, "Filter chip scope has incorrect chips count");

		testRunPage.hoverOnElement(neededChip.getRootElement());
		neededChip.clickSupportIcon();

		pause(5);
		testRunPage.clickDeleteButton();
		testRunPage.getAlert().accept();
		testRunPage.waitUntilAlertWithTextIsPresent(5);
		Assert.assertEquals(testRunPage.getSuccessAlert().getText(), "Filter was deleted", "Incorrect success alert text");

		TestRunAPIService testRunAPIService = new TestRunAPIService();
		testRunAPIService.createTestRuns(1, 2, 0, 0, 0, 0, 0).get(0);

		generatedName = "name1" + RandomUtils.nextInt(0, 10000);
		filterBlock.createFilter(generatedName, "desc1", "Status:Equals:Passed");
		testRunPage.clickCreateFilterButton();
		neededChip = filterBlock.getFilterCreatedChipByName(generatedName);
		neededChip.getRootElement().click();
		TestRun testRun = testRunMapper.searchTestRuns(new TestRunSearchCriteria() {
			{
				setStatus(Status.PASSED);
			}
		}).get(0);
		verifyTestRunInformation(testRun, 0);

		filterBlock.createFilter("name2", "desc2", "Job Url:Equals:test");
		testRunPage.clickCreateFilterButton();
		filterBlock.getFilterCreatedChipByName("name2").getRootElement().click();

		testRunPage.waitUntilPageIsLoaded();

		Assert.assertTrue(testRunPage.isElementPresent(testRunPage.getTestRunTable().getNoDataRow(), 5), "Filter is working incorrect");
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
		if(testRun.getInProgress() != 0){
			Assert.assertEquals(testRunTableRow.getInProgressCount(), testRun.getInProgress(), "Invalid in progress count");
		}
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
		TestTable testTable = testRunPage.isElementPresent(testRunTableRow.getTestTable().getRootElement(), 1)
				? testRunTableRow.getTestTable() : testRunTableRow.clickExpandTestsIcon();
		testRunPage.waitUntilPageIsLoaded();
		Assert.assertEquals(tests.size(), testRunTableRow.getTestTable().getTestRows().size(), "Invalid tests count visible");
		IntStream.iterate(0, i -> i++).limit(testTable.getTestRows().size()).forEach(i -> {
			com.qaprosoft.zafira.models.db.Test currentTest = tests.get(i);
			TestRow currentTestRow = testTable.getTestRows().get(i);
			Status status = currentTestRow.getStatus();
			Assert.assertEquals(currentTest.getStatus(), status, "Incorrect test status visible");
			Assert.assertEquals(currentTest.getName(), currentTestRow.getTestNameText(), "Invalid test name text");
			Assert.assertEquals(currentTest.getOwner(), currentTestRow.getOwnerName(), "Invalid owner");
			//Assert.assertEquals(currentTest.getTestConfig().getDevice(), currentTestRow.getDeviceName(), "Incorrect device");
			Assert.assertEquals(currentTest.getWorkItemByType(WorkItem.Type.TASK).getJiraId(), currentTestRow.getTaskTicket(), "Incorrect work item id");
			boolean isShowMoreLinkPresent = currentTestRow.isElementPresent(currentTestRow.getShowMoreLink(), 1);
			switch(status)
			{
				case ABORTED:
					Assert.assertTrue(currentTest.getMessage().length() > 100 == isShowMoreLinkPresent, "Show more link is not present");
					Assert.assertTrue(currentTest.getMessage().length() > 100 == (currentTestRow.getShowMoreLogText().length() == 100), "Show more link is not present");
					Assert.assertTrue(currentTest.getMessage().contains(currentTestRow.getShowLessLogText()), "Show more visible incorrect");
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getOpenTestDetailsModalButton(), 1), "Details button is not visible");
			    case PASSED:
			     	Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getOpenTestDetailsModalButton(), 1), "Details button is not visible");
					break;
				case FAILED:
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getOpenTestDetailsModalButton(), 1), "Details button is not visible");
					if(currentTest.getMessage().length() > 100)
					{
						Assert.assertTrue(isShowMoreLinkPresent, "Show more link is not present");
						Assert.assertEquals(currentTestRow.getShowMoreLogText().length(), 104, "Show more link is not present");
						Assert.assertTrue(currentTest.getMessage().contains(currentTestRow.getShowLessLogText()), "Show more visible incorrect");
					}
					break;
				case SKIPPED:
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getOpenTestDetailsModalButton(), 1), "Details button is not visible");
					break;
				case IN_PROGRESS:
					Assert.assertTrue(currentTestRow.isElementPresent(currentTestRow.getOpenTestDetailsModalButton(), 1), "Details button is not visible");
					break;
			}
		});
	}

	private List<TestRunViewType> generateTestRunsIfNeed(Integer searchCount, int count)
	{
		TestRunAPIService testRunAPIService = new TestRunAPIService();
		int currentCount = searchCount == null ? testRunPage.getPageItemsCount() : searchCount;
		return testRunAPIService.createTestRuns(currentCount < count ? count - currentCount : 1,
				2, 2, 0, 2, 2, 101);
	}

	private int parseCommentsCount (String str){
		return Integer.valueOf(str.substring(str.indexOf("(")+1,str.indexOf(")")));
	}
}
