package com.qaprosoft.zafira.tests.gui.pages;

import com.qaprosoft.zafira.tests.gui.components.blocks.pagination.PaginationBlock;
import com.qaprosoft.zafira.tests.gui.components.blocks.search.UserSearchBlock;
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

	private UserSearchBlock userSearchBlock;

	private PaginationBlock paginationBlock;

	public UserPage(WebDriver driver)
	{
		super(driver, "/users");
		this.userSearchBlock = new UserSearchBlock(driver, null);
		this.paginationBlock = new PaginationBlock(driver, null);
	}

	public List<WebElement> getUserMenuButtons()
	{
		return userMenuButtons;
	}

	public List<WebElement> getUserRows()
	{
		return userRows;
	}

	public UserSearchBlock getUserSearchBlock()
	{
		return userSearchBlock;
	}

	public PaginationBlock getPaginationBlock()
	{
		return paginationBlock;
	}
}
