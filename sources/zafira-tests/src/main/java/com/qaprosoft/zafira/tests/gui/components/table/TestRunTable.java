package com.qaprosoft.zafira.tests.gui.components.table;

import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.components.table.row.TestRunTableRow;

public class TestRunTable extends AbstractTable
{

	@FindBy(css = ".test-run-card")
	private List<TestRunTableRow> testRunTableRows;

	@FindBy(id = "noData")
	private WebElement noDataRow;

	public TestRunTable(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public List<TestRunTableRow> getTestRunTableRows()
	{
		return testRunTableRows;
	}

	public WebElement getNoDataRow()
	{
		return noDataRow;
	}
}
