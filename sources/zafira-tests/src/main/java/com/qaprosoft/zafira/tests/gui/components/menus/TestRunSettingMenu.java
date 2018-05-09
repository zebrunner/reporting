package com.qaprosoft.zafira.tests.gui.components.menus;

import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TestRunSettingMenu extends AbstractMenu
{

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[contains(text(), 'Open')]")
	private WebElement openButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[contains(text(), 'Copy link')]")
	private WebElement copyLinkButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[contains(text(), 'Mark as reviewed')]")
	private WebElement markAsReviewedButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[contains(text(), 'Send as email')]")
	private WebElement sendAsEmailButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[contains(text(), 'Export')]")
	private WebElement exportButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[contains(text(), 'Build now')]")
	private WebElement buildNowButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[contains(text(), 'Rebuild')]")
	private WebElement rebuildButton;

	@FindBy(xpath = "//div[preceding-sibling::header]/md-menu-content//button[contains(text(), 'Delete')]")
	private WebElement deleteButton;

	public TestRunSettingMenu(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getOpenButton()
	{
		return openButton;
	}

	public void clickOpenButton()
	{
		openButton.click();
	}

	public WebElement getCopyLinkButton()
	{
		return copyLinkButton;
	}

	public void clickCopyLinkButton()
	{
		waitUntilElementToBeClickableWithBackdropMask(copyLinkButton, 5);
		copyLinkButton.click();
	}

	public WebElement getMarkAsReviewedButton()
	{
		return markAsReviewedButton;
	}

	public WebElement getSendAsEmailButton()
	{
		return sendAsEmailButton;
	}

	public void clickMarkAsReviewedButton()
	{
		waitUntilElementToBeClickableWithBackdropMask(markAsReviewedButton, 5);
		markAsReviewedButton.click();
	}

	public void clickSendAsEmailButton()
	{
		waitUntilElementToBeClickableWithBackdropMask(sendAsEmailButton, 5);
		sendAsEmailButton.click();
	}

	public WebElement getExportButton()
	{
		return exportButton;
	}

	public void clickExportButton()
	{
		waitUntilElementToBeClickableWithBackdropMask(exportButton, 5);
		exportButton.click();
	}

	public WebElement getBuildNowButton()
	{
		return buildNowButton;
	}

	public void clickBuildNowButton()
	{
		waitUntilElementToBeClickableWithBackdropMask(buildNowButton, 5);
		buildNowButton.click();
	}

	public WebElement getRebuildButton()
	{
		return rebuildButton;
	}

	public void clickRebuildButton()
	{
		rebuildButton.click();
	}

	public WebElement getDeleteButton()
	{
		return deleteButton;
	}

	public void clickDeleteButton()
	{
		waitUntilElementToBeClickableWithBackdropMask(deleteButton, 5);
		deleteButton.click();
	}
}
