package com.qaprosoft.zafira.tests.gui.components.blocks.pagination;

import com.qaprosoft.zafira.tests.gui.components.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PaginationBlock extends AbstractUIObject
{

	protected static final String CONTAINER_LOCATOR = "//md-table-pagination";

	@FindBy(xpath = CONTAINER_LOCATOR)
	private WebElement container;

	@FindBy(xpath = CONTAINER_LOCATOR + "//*[contains(@class, 'label')]")
	private WebElement countOfPageElements;

	@FindBy(xpath = CONTAINER_LOCATOR + "//*[@aria-label = 'First']")
	private WebElement firstPageButton;

	@FindBy(xpath = CONTAINER_LOCATOR + "//*[@aria-label = 'Previous']")
	private WebElement previousPageButton;

	@FindBy(xpath = CONTAINER_LOCATOR + "//*[@aria-label = 'Next']")
	private WebElement nextPageButton;

	@FindBy(xpath = CONTAINER_LOCATOR + "//*[@aria-label = 'Last']")
	private WebElement lastPageButton;

	public PaginationBlock(WebDriver driver)
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
