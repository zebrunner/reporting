package com.qaprosoft.zafira.tests.gui.components.blocks.pagination;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;

public class PaginationBlock extends AbstractUIObject
{

	@FindBy(xpath = ".//*[contains(@class, 'label')]")
	private WebElement countOfPageElements;

	@FindBy(xpath = ".//*[@aria-label = 'First']")
	private WebElement firstPageButton;

	@FindBy(xpath = ".//*[@aria-label = 'Previous']")
	private WebElement previousPageButton;

	@FindBy(xpath = ".//*[@aria-label = 'Next']")
	private WebElement nextPageButton;

	@FindBy(xpath = ".//*[@aria-label = 'Last']")
	private WebElement lastPageButton;

	public PaginationBlock(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public String getCountOfPageElementsText()
	{
		return countOfPageElements.getText();
	}

	public void clickFirstPageButton()
	{
		firstPageButton.click();
	}

	public void clickPreviousPageButton()
	{
		previousPageButton.click();
	}

	public void clickNextPageButton()
	{
		nextPageButton.click();
	}

	public void clickLastPageButton()
	{
		lastPageButton.click();
	}

	public WebElement getCountOfPageElements()
	{
		return countOfPageElements;
	}

	public WebElement getFirstPageButton()
	{
		return firstPageButton;
	}

	public WebElement getPreviousPageButton()
	{
		return previousPageButton;
	}

	public WebElement getNextPageButton()
	{
		return nextPageButton;
	}

	public WebElement getLastPageButton()
	{
		return lastPageButton;
	}
}
