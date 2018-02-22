package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.tests.gui.components.TestRunTabMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateTestRunViewModal;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.TestRunViewPage;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import com.qaprosoft.zafira.tests.services.gui.TestRunViewPageService;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestRunViewPageTest extends AbstractTest
{

	private static final String PROJECT_NAME = "UNKNOWN";

	private TestRunViewPage testRunViewPage;
	private TestRunViewPageService testRunViewPageService;
	private String testRunViewName;

	@BeforeMethod
	public void setup()
	{
		this.testRunViewPage = new TestRunViewPage(driver);
		this.testRunViewPageService = new TestRunViewPageService(driver);
		LoginPage loginPage = new LoginPage(driver);
		LoginPageService loginPageService = new LoginPageService(driver);
		loginPage.open();
		DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		dashboardPage.waitUntilPageIsLoaded(10);
		this.testRunViewName = "test" + RandomUtils.nextInt(0, 10000);
	}

	@Test
	public void verifyCreateTestRunViewTest()
	{
		DashboardPage dashboardPage = new DashboardPage(driver);
		CreateTestRunViewModal createTestRunViewModal = dashboardPage.getNavbar().goToCreateTestRunViewModal();
		Assert.assertEquals(createTestRunViewModal.getHeaderText(), CreateTestRunViewModal.TITLE, "Incorrect title of modal window");
		Assert.assertTrue(createTestRunViewModal.hasDisabledAttribute(createTestRunViewModal.getCreateButton()), "Create button is enabled");
		Assert.assertFalse(createTestRunViewModal.isElementPresent(createTestRunViewModal.getSaveButton(), 1), "Save button is present");
		Assert.assertFalse(createTestRunViewModal.isElementPresent(createTestRunViewModal.getDeleteButton(), 1), "Delete button is present");
		Assert.assertTrue(createTestRunViewModal.getNameInput().getAttribute("value").isEmpty(), "Name input is not empty");
		createTestRunViewModal.typeName(testRunViewName);
		Assert.assertTrue(createTestRunViewModal.hasDisabledAttribute(createTestRunViewModal.getCreateButton()), "Create button is enabled");
		createTestRunViewModal.clearAllInputs();
		createTestRunViewModal.selectProject(PROJECT_NAME);
		Assert.assertTrue(createTestRunViewModal.hasDisabledAttribute(createTestRunViewModal.getCreateButton()), "Create button is enabled");
		createTestRunViewModal.typeName(testRunViewName);
		Assert.assertFalse(createTestRunViewModal.hasDisabledAttribute(createTestRunViewModal.getCreateButton()), "Create button is disabled");
		createTestRunViewModal.clickCreateButton();
		Assert.assertEquals(createTestRunViewModal.getSuccessAlert().getText(), "View created successfully", "Alert text is incorrect");
		TestRunTabMenu testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		WebElement testRunView = testRunTabMenu.getTestRunsViewByName(testRunViewName);
		Assert.assertTrue(dashboardPage.isElementPresent(testRunView, 1), "Test run view is not present");
		Assert.assertTrue(dashboardPage.isElementPresent(testRunTabMenu.getTestRunViewEditIconByName(testRunViewName), 1), "Test run view edit icon is not present");
		testRunView.click();
		testRunViewPage.waitUntilPageIsLoaded();
		Assert.assertTrue(testRunViewPage.isOpened(), "Test run view page can not open");
	}

	@Test
	public void verifyUpdateTestRunViewTest()
	{
		testRunViewPageService.createTestRunView(this.testRunViewName, PROJECT_NAME);
		DashboardPage dashboardPage = new DashboardPage(driver);
		TestRunTabMenu testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		CreateTestRunViewModal createTestRunViewModal = testRunTabMenu.clickTestRunViewEditIconByName(this.testRunViewName);
		Assert.assertEquals(createTestRunViewModal.getWebElementValue(createTestRunViewModal.getNameInput()), this.testRunViewName, "Incorrect test run view name");
		Assert.assertEquals(createTestRunViewModal.getSelectedValue(createTestRunViewModal.getProjectSelect()), PROJECT_NAME, "Incorrect project selected value");
		Assert.assertFalse(createTestRunViewModal.isElementPresent(createTestRunViewModal.getCreateButton(), 1), "Create button is present");
		Assert.assertFalse(createTestRunViewModal.hasDisabledAttribute(createTestRunViewModal.getSaveButton()), "Save button is disabled");
		Assert.assertFalse(createTestRunViewModal.hasDisabledAttribute(createTestRunViewModal.getDeleteButton()), "Delete button is disabled");
		createTestRunViewModal.clearAllInputs();
		Assert.assertTrue(createTestRunViewModal.hasDisabledAttribute(createTestRunViewModal.getSaveButton()), "Save button is not disabled");
		Assert.assertTrue(createTestRunViewModal.hasDisabledAttribute(createTestRunViewModal.getDeleteButton()), "Delete button is not disabled");
		this.testRunViewName = "test" + RandomUtils.nextInt(0, 10000);
		createTestRunViewModal.typeName(this.testRunViewName);
		createTestRunViewModal.selectProject(PROJECT_NAME);
		createTestRunViewModal.clickSaveButton();
		dashboardPage.waitUntilElementWithTextIsPresent(dashboardPage.getSuccessAlert(), "View updated successfully", 5);
		Assert.assertEquals(dashboardPage.getSuccessAlert().getText(), "View updated successfully", "Success alert has incorrect text");
		dashboardPage.waitUntilElementIsNotPresent(dashboardPage.getSuccessAlert(), 5);
		testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		WebElement testRunView = testRunTabMenu.getTestRunsViewByName(this.testRunViewName);
		createTestRunViewModal = testRunTabMenu.clickTestRunViewEditIconByName(this.testRunViewName);
		Assert.assertEquals(createTestRunViewModal.getWebElementValue(createTestRunViewModal.getNameInput()), this.testRunViewName, "Incorrect test run view name");
		Assert.assertEquals(createTestRunViewModal.getSelectedValue(createTestRunViewModal.getProjectSelect()), PROJECT_NAME, "Incorrect project selected value");
		createTestRunViewModal.clickDeleteButton();
		dashboardPage.waitUntilElementWithTextIsPresent(dashboardPage.getSuccessAlert(), "View deleted successfully", 5);
		Assert.assertEquals(dashboardPage.getSuccessAlert().getText(), "View deleted successfully", "Incorrect alert text on delete");
		testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		pause(1);
		Assert.assertFalse(testRunTabMenu.isElementPresent(testRunView, 1), "View is present after deleting");
	}
}
