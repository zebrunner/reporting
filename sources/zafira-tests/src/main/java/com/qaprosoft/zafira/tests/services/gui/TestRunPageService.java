package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.components.menus.TestRunSettingMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.BuildNowModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.MarkAsReviewedModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.RebuildModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.SendAsEmailModalWindow;
import com.qaprosoft.zafira.tests.gui.components.table.row.TestRunTableRow;
import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import org.openqa.selenium.WebDriver;

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
		testRunSettingMenu.clickSendAsEmailButton();
		return testRunPage.getSendAsEmailModalWindow();
	}

	public void clickExportButton(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunSettingMenu.clickExportButton();
	}

	public BuildNowModalWindow clickBuildNowButton(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunSettingMenu.clickBuildNowButton();
		return testRunPage.getBuildNowModalWindow();
	}

	public RebuildModalWindow clickRebuildButton(int index)
	{
		TestRunTableRow testRunTableRow = getTestRunRowByIndex(index);
		TestRunSettingMenu testRunSettingMenu = testRunTableRow.clickTestRunSettingMenu();
		testRunSettingMenu.clickRebuildButton();
		return testRunPage.getRebuildModalWindow();
	}
}
