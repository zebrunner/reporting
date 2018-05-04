package com.qaprosoft.zafira.tests.gui.components.modals.testrun;

import com.qaprosoft.zafira.tests.gui.components.modals.AbstractModalWindow;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MarkAsReviewedModalWindow extends AbstractModalWindow
{

	@FindBy(id = "comment")
	private WebElement commentInput;

	@FindBy(id = "markAsReviewed")
	private WebElement markAsReviewedButton;

	public MarkAsReviewedModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getCommentInput()
	{
		return commentInput;
	}

	public void typeComment(String comment)
	{
		commentInput.click();
		commentInput.sendKeys(comment);
		commentInput.click();
		pause(0.2);
	}

	public WebElement getMarkAsReviewedButton()
	{
		return markAsReviewedButton;
	}

	public void clickMarkAsReviewedButton()
	{
		markAsReviewedButton.click();
		waitUntilElementIsNotPresent(commentInput, IMPLICITLY_TIMEOUT);
	}
}
