package com.qaprosoft.zafira.tests.gui.pages;

import com.qaprosoft.zafira.tests.gui.components.blocks.pagination.PaginationBlock;
import com.qaprosoft.zafira.tests.gui.components.blocks.search.TestRunSearchBlock;
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

	public TestRunPage(WebDriver driver)
	{
		super(driver, "/tests/runs");
	}

}
