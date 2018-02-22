package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.components.modals.CreateTestRunViewModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.JobViewSettingModalWindow;
import com.qaprosoft.zafira.tests.gui.pages.TestRunViewPage;
import org.openqa.selenium.WebDriver;

public class TestRunViewPageService extends AbstractPageService
{

	private TestRunViewPage testRunViewPage;

	public TestRunViewPageService(WebDriver driver)
	{
		super(driver);
		this.testRunViewPage = new TestRunViewPage(driver);
	}

	public void createTestRunView(String name, String project)
	{
		CreateTestRunViewModalWindow createTestRunViewModalWindow = testRunViewPage.getNavbar().goToCreateTestRunViewModal();
		createTestRunViewModalWindow.typeName(name);
		createTestRunViewModalWindow.selectProject(project);
		createTestRunViewModalWindow.clickCreateButton();
	}

	public JobViewSettingModalWindow goToCreateViewTableModalWindow()
	{
		testRunViewPage.goToFabButtonByClassName("plus");
		return testRunViewPage.getJobViewSettingModalWindow();
	}
}
