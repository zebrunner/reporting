package com.qaprosoft.zafira.tests.gui.components.modals.view;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class JobViewSettingModalWindowFilterBox extends AbstractUIObject
{

	@FindBy(xpath = ".//*[@type = 'checkbox']")
	private WebElement jobCheckbox;

	@FindBy(xpath = ".//label")
	private WebElement jobName;

	public JobViewSettingModalWindowFilterBox(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getJobCheckbox()
	{
		return jobCheckbox;
	}

	public void checkJobCheckbox()
	{
		check(jobCheckbox);
	}

	public void uncheckJobCheckbox()
	{
		uncheck(jobCheckbox);
	}

	public WebElement getJobName()
	{
		return jobName;
	}

	public String getJobNameText()
	{
		return jobName.getText();
	}
}
