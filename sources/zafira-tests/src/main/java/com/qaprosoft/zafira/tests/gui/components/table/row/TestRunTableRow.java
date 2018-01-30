package com.qaprosoft.zafira.tests.gui.components.table.row;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TestRunTableRow extends AbstractRow
{

	@FindBy(xpath = "./td[1]//small")
	private WebElement percentage;

	@FindBy(xpath = "./td[1]//md-progress-circular")
	private WebElement progressCircularIcon;

	public TestRunTableRow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}
}
