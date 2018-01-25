package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.components.menus.UserSettingMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class AbstractPageWithTableService extends AbstractPageService
{

	protected AbstractPageWithTableService(WebDriver driver)
	{
		super(driver);
	}

	public WebElement getUserMenuButtonByIndex(int index)
	{
		return getRowMenuButton(index);
	}

	public UserSettingMenu clickUserMenuButtonByIndex(int index)
	{
		getUserMenuButtonByIndex(index).click();
		UserSettingMenu userSettingMenu = new UserSettingMenu(driver);
		userSettingMenu.waitUntilElementToBeClickableByBackdropMask(userSettingMenu.getElement(), 1);
		return userSettingMenu;
	}

	protected WebElement getTableRowByIndex(int index)
	{
		return driver.findElement(By.xpath("(.//tbody//tr)[" + index + "]"));
	}

	protected WebElement getTableColumnByIndex(int index, int columnNumber)
	{
		WebElement userRow = getTableRowByIndex(index);
		return userRow.findElement(By.xpath(".//td[" + columnNumber + "]"));
	}

	protected WebElement getLastTableColumnByIndex(int index)
	{
		WebElement userRow = getTableRowByIndex(index);
		return userRow.findElement(By.xpath(".//td[last()]"));
	}

	protected WebElement getRowMenuButton(int index)
	{
		return getLastTableColumnByIndex(index).findElement(By.tagName("button"));
	}
}
