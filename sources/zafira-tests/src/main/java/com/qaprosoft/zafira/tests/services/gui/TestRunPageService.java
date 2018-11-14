package com.qaprosoft.zafira.tests.services.gui;

import org.openqa.selenium.WebDriver;

import com.qaprosoft.zafira.tests.gui.components.blocks.search.TestRunSearchBlock;
import com.qaprosoft.zafira.tests.gui.components.menus.TestRunSettingMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.BuildNowModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.MarkAsReviewedModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.RebuildModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.SendAsEmailModalWindow;
import com.qaprosoft.zafira.tests.gui.components.table.TestTable;
import com.qaprosoft.zafira.tests.gui.components.table.row.TestRunTableRow;
import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;

public class TestRunPageService extends AbstractPageService
{

	private TestRunPage testRunPage;

	public TestRunPageService(WebDriver driver)
	{
		super(driver);
		this.testRunPage = new TestRunPage(driver);
	}

	public TestRunPage getTestRunPage()
	{
		return testRunPage;
	}

	public TestRunTableRow getTestRunRowByIndex(int index)
	{
		return testRunPage.getTestRunTable().getTestRunTableRows().get(index);
	}

	public MarkAsReviewedModalWindow clickMarkAsReviewedButton(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunPage.getMarkAsReviewedModalWindow().waitUntilElementToBeClickableWithBackdropMask(testRunPage.getMarkAsReviewedModalWindow().getMarkAsReviewedButton(), 1);
		testRunSettingMenu.clickMarkAsReviewedButton();
		return testRunPage.getMarkAsReviewedModalWindow();
	}

	public MarkAsReviewedModalWindow clickCommentIcon(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		testRunTableRow.clickCommentIcon();
		return testRunPage.getMarkAsReviewedModalWindow();
	}

	public SendAsEmailModalWindow clickSendAsEmailButton(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunPage.waitUntilElementToBeClickableWithBackdropMask(testRunSettingMenu.getSendAsEmailButton(), 1);
		testRunSettingMenu.clickSendAsEmailButton();
		return testRunPage.getSendAsEmailModalWindow();
	}

	public void clickExportButton(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunPage.waitUntilElementToBeClickableWithBackdropMask(testRunSettingMenu.getExportButton(), 2);
		testRunSettingMenu.clickExportButton();
	}

	public BuildNowModalWindow clickBuildNowButton(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunPage.waitUntilElementToBeClickableWithBackdropMask(testRunSettingMenu.getBuildNowButton(), 2);
		testRunSettingMenu.clickBuildNowButton();
		testRunPage.getBuildNowModalWindow().waitUntilModalIsNotPresent();
		return testRunPage.getBuildNowModalWindow();
	}

	public RebuildModalWindow clickRebuildButton(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunPage.waitUntilElementToBeClickableWithBackdropMask(testRunSettingMenu.getRebuildButton(), 5);
		testRunSettingMenu.clickRebuildButton();
		return testRunPage.getRebuildModalWindow();
	}

	public TestRunPage search(String status, String testSuite, String jobUrl, String environment, boolean reviewed, String platform,
			String appVersion)
	{
		TestRunSearchBlock testRunSearchBlock = testRunPage.getTestRunSearchBlock();
		testRunSearchBlock.selectStatus(status);
		testRunSearchBlock.typeTestSuiteName(testSuite);
		testRunSearchBlock.typeJobURL(jobUrl);
		testRunSearchBlock.selectEnvironment(environment);
		if(reviewed)
			testRunSearchBlock.clickReviewedCheckbox();
		testRunSearchBlock.selectPlatform(platform);
		testRunSearchBlock.typeAppVersion(appVersion);
		if(testRunSearchBlock.isElementPresent(testRunSearchBlock.getSearchButton(), 2)) {
			testRunSearchBlock.clickSearchButton();
			testRunPage.waitUntilPageIsLoaded();
		}
		return testRunPage;
	}

	public TestTable getTestTableByRowIndex(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		TestTable result;
		if(! testRunPage.isElementPresent(testRunTableRow.getTestTable().getRootElement(), 1))
		{
			testRunPage.hoverOnElement(testRunTableRow.getRootElement());
			testRunPage.waitUntilElementIsPresent(testRunTableRow.getExpandTestsIcon(), 1);
			result = testRunTableRow.clickExpandTestsIcon();
		} else
		{
			result = testRunTableRow.getTestTable();
		}
		return result;
	}

	public TestRunPage clearSearchForm()
	{
		testRunPage.getTestRunSearchBlock().clickClearButton();
		testRunPage.waitUntilPageIsLoaded();
		return testRunPage;
	}

	public TestRunPage goToFirstPage()
	{
		testRunPage.getPaginationBlock().clickFirstPageButton();
		testRunPage.waitUntilPageIsLoaded();
		return testRunPage;
	}

	public TestRunPage goToPreviousPage()
	{
		testRunPage.getPaginationBlock().clickPreviousPageButton();
		testRunPage.waitUntilPageIsLoaded();
		return testRunPage;
	}

	public TestRunPage goToNextPage()
	{
		testRunPage.getPaginationBlock().clickNextPageButton();
		testRunPage.waitUntilPageIsLoaded();
		return testRunPage;
	}

	public TestRunPage goToLastPage()
	{
		testRunPage.getPaginationBlock().clickLastPageButton();
		testRunPage.waitUntilPageIsLoaded();
		return testRunPage;
	}

	public int getTestRunTableRowsCount()
	{
		return testRunPage.getTestRunTable().getTestRunTableRows().size();
	}
}
