package com.qaprosoft.zafira.tests.gui.components.blocks.search;

import com.qaprosoft.zafira.tests.gui.components.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class AbstractSearchBlock extends AbstractUIObject
{

	protected static final String CONTAINER_LOCATOR = "//thead";

	@FindBy(xpath = CONTAINER_LOCATOR)
	private WebElement container;

	@FindBy(xpath = "//button[./*[text() = 'Search']]")
	protected WebElement searchButton;

	@FindBy(xpath = "//button[./*[text() = 'Clear']]")
	protected WebElement clearButton;

	protected AbstractSearchBlock(WebDriver driver)
	{
		super(driver, null);
	}

	@Override
	public By getLocator()
	{
		return By.xpath(CONTAINER_LOCATOR);
	}

	@Override
	public WebElement getElement()
	{
		return container;
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
