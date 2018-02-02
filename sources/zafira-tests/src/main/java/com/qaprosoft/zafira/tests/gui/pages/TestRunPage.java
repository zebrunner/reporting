package com.qaprosoft.zafira.tests.gui.pages;

import com.qaprosoft.zafira.tests.gui.components.FabButton;
import com.qaprosoft.zafira.tests.gui.components.blocks.pagination.PaginationBlock;
import com.qaprosoft.zafira.tests.gui.components.blocks.search.TestRunSearchBlock;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.BuildNowModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.MarkAsReviewedModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.RebuildModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.testrun.SendAsEmailModalWindow;
import com.qaprosoft.zafira.tests.gui.components.table.TestRunTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class TestRunPage extends BasePage
{

	@FindBy(xpath = ".//thead")
	private TestRunSearchBlock testRunSearchBlock;

	@FindBy(xpath = ".//tbody[@md-body]")
	private TestRunTable testRunTable;

	@FindBy(xpath = ".//md-table-pagination")
	private PaginationBlock paginationBlock;

	@FindBy(xpath = "//md-fab-speed-dial")
	private FabButton fabButton;

	@FindBy(xpath = ".//md-dialog")
	private MarkAsReviewedModalWindow markAsReviewedModalWindow;

	@FindBy(xpath = ".//md-dialog")
	private SendAsEmailModalWindow sendAsEmailModalWindow;

	@FindBy(xpath = ".//md-dialog")
	private BuildNowModalWindow buildNowModalWindow;

	@FindBy(xpath = ".//md-dialog")
	private RebuildModalWindow rebuildModalWindow;

	public TestRunPage(WebDriver driver)
	{
		super(driver, "/tests/runs");
	}

	public TestRunSearchBlock getTestRunSearchBlock()
	{
		return testRunSearchBlock;
	}

	public TestRunTable getTestRunTable()
	{
		return testRunTable;
	}

	public PaginationBlock getPaginationBlock()
	{
		return paginationBlock;
	}

	public FabButton getFabButton()
	{
		return fabButton;
	}

	public MarkAsReviewedModalWindow getMarkAsReviewedModalWindow()
	{
		return markAsReviewedModalWindow;
	}

	public SendAsEmailModalWindow getSendAsEmailModalWindow()
	{
		return sendAsEmailModalWindow;
	}

	public BuildNowModalWindow getBuildNowModalWindow()
	{
		return buildNowModalWindow;
	}

	public RebuildModalWindow getRebuildModalWindow()
	{
		return rebuildModalWindow;
	}
}
