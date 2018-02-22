package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateTestRunViewModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.TestRunPage;
import com.qaprosoft.zafira.tests.gui.pages.TestRunViewPage;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

public class TestRunTabMenu extends AbstractUIObject
{

	private static final String SHOW_RUNS_BUTTON_TEXT = "Show latest runs";

	@FindBy(xpath = ".//a[text() = 'Create view +']")
	private WebElement createViewButton;

	@FindBy(xpath = "//md-dialog")
	private CreateTestRunViewModalWindow createTestRunViewModalWindow;

	public TestRunTabMenu(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public List<String> getTestRunsViews()
	{
		return context.findElements(By.tagName("a")).stream()
				.filter(webElement -> ! webElement.getText().equals(SHOW_RUNS_BUTTON_TEXT))
				.map(WebElement::getText).collect(Collectors.toList());
	}

	public WebElement getTestRunsViewByName(String name)
	{
		return context.findElement(By.xpath(".//a[text() = '" + name + "']"));
	}

	public WebElement getTestRunViewEditIconByName(String name)
	{
		return getTestRunsViewByName(name).findElement(By.xpath("./following-sibling::i[contains(@class, 'edit')]"));
	}

	public CreateTestRunViewModalWindow clickTestRunViewEditIconByName(String name)
	{
		getTestRunViewEditIconByName(name).click();
		return createTestRunViewModalWindow;
	}

	public WebElement getCreateViewButton()
	{
		return createViewButton;
	}

	public CreateTestRunViewModalWindow getCreateTestRunViewModalWindow()
	{
		return createTestRunViewModalWindow;
	}

	public TestRunViewPage clickTestRunViewByName(String name)
	{
		getTestRunsViewByName(name).click();
		return new TestRunViewPage(driver);
	}

	public CreateTestRunViewModalWindow clickCreateTestRunViewButton()
	{
		createViewButton.click();
		return createTestRunViewModalWindow;
	}

	public TestRunPage clickShowRunsButton()
	{
		getTestRunsViewByName(SHOW_RUNS_BUTTON_TEXT).click();
		return new TestRunPage(driver);
	}
}
