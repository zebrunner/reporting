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

	protected AbstractModalWindow(WebDriver driver)
	{
		super(driver, null);
		waitUntilElementIsPresent(getCloseButton(), 4);
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
		return waitUntilElementIsNotPresent(getBackdrop(), 4);
	}

	public void clearAllInputs()
	{
		container.findElements(By.xpath(".//input[not(@type = 'checkbox') and not(@disabled)]")).forEach(WebElement::clear);
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
