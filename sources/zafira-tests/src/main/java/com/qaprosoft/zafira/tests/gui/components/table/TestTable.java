package com.qaprosoft.zafira.tests.gui.components.table;

import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.components.blocks.sort.TestSortBlock;
import com.qaprosoft.zafira.tests.gui.components.table.row.TestRow;

public class TestTable extends AbstractTable
{

	@FindBy(xpath = ".//thead")
	private TestSortBlock testSortBlock;

	@FindBy(xpath = ".//tr[contains(@class, 'test ')]")
	private List<TestRow> testRows;

	public TestTable(WebDriver driver, SearchContext context)
	{
		super(driver, driver);
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
