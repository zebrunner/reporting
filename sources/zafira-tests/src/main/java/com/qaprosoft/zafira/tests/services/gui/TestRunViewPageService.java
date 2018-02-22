package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.components.modals.CreateTestRunViewModal;
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
		CreateTestRunViewModal createTestRunViewModal = testRunViewPage.getNavbar().goToCreateTestRunViewModal();
		createTestRunViewModal.typeName(name);
		createTestRunViewModal.selectProject(project);
		createTestRunViewModal.clickCreateButton();
	}
}
