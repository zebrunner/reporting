package com.qaprosoft.zafira.tests.gui.components.blocks.sort;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TestSortBlock extends AbstractUIObject
{

	@FindBy(xpath = ".//span[contains(text(), 'Status')]")
	private WebElement statusButton;

	@FindBy(xpath = ".//span[contains(text(), 'Title')]")
	private WebElement titleButton;

	@FindBy(xpath = ".//span[contains(text(), 'Owner')]")
	private WebElement ownerButton;

	@FindBy(xpath = ".//span[contains(text(), 'Device')]")
	private WebElement deviceButton;

	@FindBy(xpath = ".//span[contains(text(), 'Elapsed')]")
	private WebElement elapsedButton;

	@FindBy(xpath = ".//span[contains(text(), 'Started')]")
	private WebElement startedButton;

	protected TestSortBlock(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public void clickStatusButton()
	{
		statusButton.click();
	}

	public WebElement getStatusButton()
	{
		return statusButton;
	}

	public void clickTitleButton()
	{
		titleButton.click();
	}

	public WebElement getTitleButton()
	{
		return titleButton;
	}

	public WebElement getOwnerButton()
	{
		return ownerButton;
	}

	public void clickOwnerButton()
	{
		ownerButton.click();
	}

	public WebElement getDeviceButton()
	{
		return deviceButton;
	}

	public void clickDeviceButton()
	{
		deviceButton.click();
	}

	public WebElement getElapsedButton()
	{
		return elapsedButton;
	}

	public void clickElapsedButton()
	{
		elapsedButton.click();
	}

	public WebElement getStartedButton()
	{
		return startedButton;
	}

	public void clickStartedButton()
	{
		statusButton.click();
	}
}
