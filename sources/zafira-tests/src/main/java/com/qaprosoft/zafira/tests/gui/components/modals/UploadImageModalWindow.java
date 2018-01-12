package com.qaprosoft.zafira.tests.gui.components.modals;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class UploadImageModalWindow extends AbstractModalWindow
{

	@FindBy(xpath = CONTAINER_LOCATOR + "//input")
	public WebElement imageUploadInput;

	@FindBy(xpath = CONTAINER_LOCATOR + "//button")
	public WebElement uploadImageButton;

	public UploadImageModalWindow(WebDriver driver, String path)
	{
		super(driver, path);
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
