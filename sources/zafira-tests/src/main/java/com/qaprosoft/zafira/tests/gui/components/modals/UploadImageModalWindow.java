package com.qaprosoft.zafira.tests.gui.components.modals;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UploadImageModalWindow extends AbstractModalWindow
{

	@FindBy(xpath = ".//input")
	private WebElement imageUploadInput;

	@FindBy(xpath = ".//button")
	private WebElement uploadImageButton;

	public UploadImageModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getImageUploadInput()
	{
		return imageUploadInput;
	}

	public WebElement getUploadImageButton()
	{
		return uploadImageButton;
	}
}
