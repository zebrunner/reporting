package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardTabMenu extends AbstractUIObject
{

	private static final String CONTAINER_LOCATOR = "//*[@id  ='nav']//ul[preceding-sibling::a[.//*[text()='Dashboards']]]";

	@FindBy(xpath = CONTAINER_LOCATOR)
	private WebElement container;

	public DashboardTabMenu(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public List<String> getDashboardNames()
	{
		return findElements(By.tagName("a")).stream().map(WebElement::getText).collect(Collectors.toList());
	}

	public WebElement getDashboardByName(String name)
	{
		return findElement(By.xpath(".//a[text() = '" + name + "']"));
	}

	public DashboardPage clickDashboardByName(String name)
	{
		getDashboardByName(name).click();
		return new DashboardPage(driver, null);
	}

	public boolean isProjectIsHidden(String name)
	{
		return isElementPresent(getDashboardByName(name), By.xpath("./i[contains(@class, 'fa')]"), 1);
	}

	@Override
	public By getLocator()
	{
		return By.xpath(CONTAINER_LOCATOR);
	}

	@Override
	public WebElement getElement()
	{
		return this.container;
	}
}
