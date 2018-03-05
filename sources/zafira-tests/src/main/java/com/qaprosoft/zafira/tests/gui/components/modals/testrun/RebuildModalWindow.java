package com.qaprosoft.zafira.tests.gui.components.modals.testrun;

import com.qaprosoft.zafira.tests.gui.components.modals.AbstractModalWindow;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RebuildModalWindow extends AbstractModalWindow
{

	@FindBy(id = "onlyFailures")
	private WebElement onlyFailuresRadioButton;

	@FindBy(id = "allTests")
	private WebElement allTestsRadioButton;

	@FindBy(id = "cancel")
	private WebElement cancelButton;

	@FindBy(id = "rerun")
	private WebElement rerunButton;

	public RebuildModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}


	public WebElement getOnlyFailuresRadioButton()
	{
		return onlyFailuresRadioButton;
	}

	public void clickOnlyFailuresRadioButton()
	{
		waitUntilElementToBeClickable(onlyFailuresRadioButton, 2);
		onlyFailuresRadioButton.click();
	}

	public WebElement getAllTestsRadioButton()
	{
		return allTestsRadioButton;
	}

	public void clickAllTestsRadioButton()
	{
		waitUntilElementToBeClickable(allTestsRadioButton, 2);
		allTestsRadioButton.click();
	}

	public WebElement getCancelButton()
	{
		return cancelButton;
	}

	public void clickCancelButton()
	{
		cancelButton.click();
		pause(1);
	}

	public WebElement getRerunButton()
	{
		return rerunButton;
	}

	public void clickRerunButton()
	{
		rerunButton.click();
	}
}
