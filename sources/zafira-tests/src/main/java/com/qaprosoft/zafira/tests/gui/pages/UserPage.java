package com.qaprosoft.zafira.tests.gui.pages;

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
}
