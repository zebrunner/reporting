package com.qaprosoft.zafira.tests.gui.components.table;

import com.qaprosoft.zafira.tests.gui.components.blocks.sort.TestSortBlock;
import com.qaprosoft.zafira.tests.gui.components.table.row.TestRow;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TestTable extends AbstractTable
{

	@FindBy(xpath = "./thead")
	private TestSortBlock testSortBlock;

	@FindBy(xpath = "./tbody/tr[@data-ng-repeat]")
	private List<TestRow> testRows;

	protected TestTable(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public TestSortBlock getTestSortBlock()
	{
		return testSortBlock;
	}

	public List<TestRow> getTestRows()
	{
		return testRows;
	}
}
