package com.qaprosoft.zafira.tests.gui.components;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;

public class Chip extends AbstractUIObject
{

	@FindBy(xpath = ".//img | .//i")
	private WebElement userPhotoIcon;

	@FindBy(xpath = ".//md-chip-template")
	private WebElement content;

	@FindBy(xpath = ".//button[contains(@class, 'md-chip-remove')] | .//button[@md-chip-remove]")
	private WebElement closeButton;

	@FindBy(className = "material-icons")
	private WebElement supportIcon;

	public Chip(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getUserPhotoIcon()
	{
		return userPhotoIcon;
	}

	public WebElement getContent()
	{
		return content;
	}

	public String getContentText(boolean replaceChildText)
	{
		return getCurrentNodeText(content, replaceChildText);//.substring(content.getText().indexOf(" ")).trim();
	}

	public WebElement getCloseButton()
	{
		return closeButton;
	}

	public void clickCloseButton()
	{
		closeButton.click();
	}

	public WebElement getSupportIcon()
	{
		return supportIcon;
	}

	public void clickSupportIcon()
	{
		supportIcon.click();
	}
}
