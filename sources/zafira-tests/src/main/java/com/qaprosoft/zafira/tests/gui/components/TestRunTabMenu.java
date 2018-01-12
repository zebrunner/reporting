package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class TestRunTabMenu extends AbstractUIObject
{
	private static final String CONTAINER_LOCATOR = "//*[@id  ='nav']//ul[preceding-sibling::a[.//*[text()='Test runs']]]";

	private static final String SHOW_RUNS_BUTTON_TEXT = "Show latest runs";

	@FindBy(xpath = CONTAINER_LOCATOR)
	private WebElement container;

	protected TestRunTabMenu(WebDriver driver, String path)
	{
		super(driver, path);
	}

	public List<String> getTestRunsViews()
	{
		return findElements(By.tagName("a")).stream()
				.filter(webElement -> ! webElement.getText().equals(SHOW_RUNS_BUTTON_TEXT))
				.map(WebElement::getText).collect(Collectors.toList());
	}

	public WebElement getTestRunsViewByName(String name)
	{
		return findElement(By.xpath(".//a[.//*[text() = '" + name + "']]"));
	}

	public DashboardPage clickTestRunsViewByName(String name)
	{
		getTestRunsViewByName(name).click();
		return new DashboardPage(driver);
	}

	public TestRunPage clickShowRunsButton()
	{
		getTestRunsViewByName(SHOW_RUNS_BUTTON_TEXT).click();
		return new TestRunPage(driver);
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
