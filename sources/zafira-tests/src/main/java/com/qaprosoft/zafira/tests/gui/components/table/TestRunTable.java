package com.qaprosoft.zafira.tests.gui.components.table;

import com.qaprosoft.zafira.tests.gui.components.table.row.TestRunTableRow;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class TestRunTable extends AbstractTable
{

	@FindBy(xpath = "./tr[@md-row]")
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
