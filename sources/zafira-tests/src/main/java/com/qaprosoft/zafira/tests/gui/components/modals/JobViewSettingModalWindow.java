package com.qaprosoft.zafira.tests.gui.components.modals;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.components.modals.view.JobViewSettingModalWindowFilterBox;

public class JobViewSettingModalWindow extends AbstractModalWindow
{

	public static final String TITLE = "Job view settings";

	@FindBy(id = "env")
	private WebElement envInput;

	@FindBy(id = "size")
	private WebElement sizeSelect;

	@FindBy(id = "position")
	private WebElement positionInput;

	@FindBy(name = "jobFilter")
	private WebElement jobFilterInput;

	@FindBy(xpath = ".//*[contains(@class, 'modal-body')]/div")
	private List<JobViewSettingModalWindowFilterBox> jobViewSettingModalWindowFilterBoxes;

	@FindBy(id = "create")
	private WebElement createButton;

	public JobViewSettingModalWindow(WebDriver driver, SearchContext context)
	{
		super(driver, context);
	}

	public WebElement getEnvInput()
	{
		return envInput;
	}

	public void typeEnv(String env)
	{
		envInput.sendKeys(env);
	}

	public WebElement getSizeSelect()
	{
		return sizeSelect;
	}

	public void selectSize(String size)
	{
		select(sizeSelect, size);
	}

	public WebElement getPositionInput()
	{
		return positionInput;
	}

	public void typePosition(String position)
	{
		positionInput.sendKeys(position);
	}

	public WebElement getJobFilterInput()
	{
		return jobFilterInput;
	}

	public void typeJobInJobFilter(String job)
	{
		jobFilterInput.sendKeys(job);
	}

	public List<JobViewSettingModalWindowFilterBox> getJobViewSettingModalWindowFilterBoxes()
	{
		return jobViewSettingModalWindowFilterBoxes;
	}

	public List<JobViewSettingModalWindowFilterBox> getJobViewSettingModalWindowFilterBoxesByNames(List<String> names)
	{
		return jobViewSettingModalWindowFilterBoxes.stream().filter(box -> names.contains(box.getJobNameText()))
				.collect(Collectors.toList());
	}

	public WebElement getCreateButton()
	{
		return createButton;
	}

	public void clickCreateButton()
	{
		createButton.click();
	}
}
