package com.qaprosoft.zafira.tests.gui.components.modals;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class AbstractModalWindow extends AbstractUIObject
{

	@FindBy(id = "modalTitle")
	protected WebElement headerTextBlock;

	@FindBy(id = "close")
	protected WebElement closeButton;

	protected AbstractModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
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

	public boolean closeModalWindow() {
		waitUntilElementToBeClickable(closeButton, 4);
		closeButton.click();
		return waitUntilModalIsNotPresent();
	}

	public boolean waitUntilModalIsNotPresent()
	{
		pause(1.4);
		return waitUntilElementIsNotPresent(getBackdrop(), 4);
	}
}
