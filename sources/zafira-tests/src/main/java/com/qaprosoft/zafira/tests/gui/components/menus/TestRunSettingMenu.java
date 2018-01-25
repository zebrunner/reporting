package com.qaprosoft.zafira.tests.gui.components.menus;

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

	protected TestRunSettingMenu(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public WebElement getOpenButton()
	{
		return openButton;
	}

	public WebElement getCopyLinkButton()
	{
		return copyLinkButton;
	}

	public WebElement getMarkAsReviewedButton()
	{
		return markAsReviewedButton;
	}

	public WebElement getSendAsEmailButton()
	{
		return sendAsEmailButton;
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
