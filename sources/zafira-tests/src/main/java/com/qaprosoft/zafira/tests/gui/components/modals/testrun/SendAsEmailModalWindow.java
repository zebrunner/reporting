package com.qaprosoft.zafira.tests.gui.components.modals.testrun;

import com.qaprosoft.zafira.tests.gui.components.Chip;
import com.qaprosoft.zafira.tests.gui.components.modals.AbstractModalWindow;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class SendAsEmailModalWindow extends AbstractModalWindow
{

	@FindBy(xpath = ".//input[@type = 'search']")
	private WebElement recipientsInput;

	@FindBy(xpath = ".//md-progress-linear")
	private WebElement progressLinear;

	@FindBy(id = "send")
	private WebElement sendButton;

	@FindBy(xpath = "//md-virtual-repeat-container//li")
	private List<WebElement> suggestions;

	@FindBy(xpath = ".//md-chip")
	private List<Chip> chips;

	public SendAsEmailModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getRecipientsInput()
	{
		return recipientsInput;
	}

	public void typeRecipients(String recipient)
	{
		recipientsInput.click();
		recipientsInput.sendKeys(recipient);
	}

	public WebElement getProgressLinear()
	{
		return progressLinear;
	}

	public WebElement getSendButton()
	{
		return sendButton;
	}

	public void clickSendButton()
	{
		sendButton.click();
	}

	public List<WebElement> getSuggestions()
	{
		return suggestions;
	}

	public void clickSuggestion(int index)
	{
		if(! isElementPresent(suggestions.get(index), 1))
			pause(0.5);
		recipientsInput.click();
		suggestions.get(index).click();
	}

	public List<Chip> getChips()
	{
		return chips;
	}
}
