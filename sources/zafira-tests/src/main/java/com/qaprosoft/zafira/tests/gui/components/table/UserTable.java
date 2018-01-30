package com.qaprosoft.zafira.tests.gui.components.table;

import com.qaprosoft.zafira.tests.gui.components.table.row.UserTableRow;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class UserTable extends AbstractTable
{

	@FindBy(xpath = ".//tr")
	private List<UserTableRow> userTableRows;

	public UserTable(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public List<UserTableRow> getUserTableRows()
	{
		return userTableRows;
	}
}
