package com.qaprosoft.zafira.tests.gui.components.blocks.search;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public abstract class AbstractSearchBlock extends AbstractUIObject
{

	protected static final String CONTAINER_LOCATOR = "//thead";

	@FindBy(xpath = "//button[./*[text() = 'APPLY']]")
	protected WebElement searchButton;

	@FindBy(xpath = "//button[./*[text() = 'RESET']]")
	protected WebElement clearButton;

	protected AbstractSearchBlock(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public void clickSearchButton()
	{
		searchButton.click();
	}

	public void clickClearButton()
	{
		clearButton.click();
	}

	public WebElement getSearchButton()
	{
		return searchButton;
	}

	public WebElement getClearButton()
	{
		return clearButton;
	}

	protected boolean isBlank(String value)
	{
		return StringUtils.isBlank(value);
	}
}
