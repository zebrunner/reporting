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
		markAsReviewedButton.click();
	}

	public WebElement getExportButton()
	{
		return exportButton;
	}

	public WebElement getBuildNowButton()
	{
		return buildNowButton;
	}

	public WebElement getRebuildButton()
	{
		return rebuildButton;
	}

	public WebElement getDeleteButton()
	{
		return deleteButton;
	}
}
