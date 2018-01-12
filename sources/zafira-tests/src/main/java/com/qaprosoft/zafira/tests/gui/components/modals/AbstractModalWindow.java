package com.qaprosoft.zafira.tests.gui.components.modals;

import com.qaprosoft.zafira.tests.gui.components.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class AbstractModalWindow extends AbstractUIObject
{

	protected static final String CONTAINER_LOCATOR = "//md-dialog";

	@FindBy(xpath = CONTAINER_LOCATOR)
	protected WebElement container;

	@FindBy(xpath = CONTAINER_LOCATOR + "//md-toolbar//h2")
	protected WebElement headerTextBlock;

	@FindBy(xpath = CONTAINER_LOCATOR + "//md-toolbar//md-icon[text() = 'close']")
	protected WebElement closeButton;

	protected AbstractModalWindow(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public WebElement getHeaderTextBlock()
	{
		return headerTextBlock;
	}

	public String getHeaderText() {
		return headerTextBlock.getText();
	}

	public WebElement getCloseButton()
	{
		return closeButton;
	}

	public void closeModalWindow() {
		closeButton.click();
	}

	@Override
	public By getLocator()
	{
		return By.xpath(CONTAINER_LOCATOR);
	}

	@Override
	public WebElement getElement()
	{
		return this.container;
	}
}
