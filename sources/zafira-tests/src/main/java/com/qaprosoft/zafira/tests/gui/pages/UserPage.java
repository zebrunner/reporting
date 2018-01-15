package com.qaprosoft.zafira.tests.gui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class UserPage extends BasePage
{

	@FindBy(xpath = "//section//md-menu")
	private List<WebElement> userMenuButtons;

	@FindBy(xpath = "//tbody//tr")
	private List<WebElement> userRows;

	public UserPage(WebDriver driver)
	{
		super(driver, "/users");
	}

	public List<WebElement> getUserMenuButtons()
	{
		return userMenuButtons;
	}

	public List<WebElement> getUserRows()
	{
		return userRows;
	}

	public WebElement getUserRowByUserId(long id)
	{
		return driver.findElement(By.xpath("//tbody//tr[.//*[@aria-label = '#" + id + "']]"));
	}

	public String getUsernameById(long id)
	{
		return getTableColumnById(id, 2).getText();
	}

	public String getEmailById(long id)
	{
		return getTableColumnById(id, 3).getText();
	}

	public String getFirstLastNameById(long id)
	{
		return getTableColumnById(id, 4).getText();
	}

	public String getStatusById(long id)
	{
		return getTableColumnById(id, 5).getText();
	}

	public String getRegistrationDateById(long id)
	{
		return getTableColumnById(id, 6).findElement(By.tagName("span")).getText();
	}

	public String getLastLoginTextById(long id)
	{
		return getTableColumnById(id, 6).findElement(By.tagName("b")).getText();
	}

	public WebElement getUserMenuButtonById(long id)
	{
		return getTableColumnById(id, 7).findElement(By.tagName("button"));
	}

	public WebElement getTableColumnById(long id, int columnNumber)
	{
		return getUserRowByUserId(id).findElement(By.xpath(".//td[" + columnNumber + "]"));
	}
}
