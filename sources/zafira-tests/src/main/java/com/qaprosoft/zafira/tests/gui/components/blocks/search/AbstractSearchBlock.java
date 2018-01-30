package com.qaprosoft.zafira.tests.gui.components.blocks.search;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class AbstractSearchBlock extends AbstractUIObject
{

	protected static final String CONTAINER_LOCATOR = "//thead";

	@FindBy(xpath = "//button[./*[text() = 'Search']]")
	protected WebElement searchButton;

	@FindBy(xpath = "//button[./*[text() = 'Clear']]")
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
}
