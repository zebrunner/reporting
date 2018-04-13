package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.dbaccess.dao.mysql.JobMapper;
import com.qaprosoft.zafira.tests.gui.components.TestRunTabMenu;
import com.qaprosoft.zafira.tests.gui.components.modals.CreateTestRunViewModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.JobViewSettingModalWindow;
import com.qaprosoft.zafira.tests.gui.components.modals.view.JobViewSettingModalWindowFilterBox;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.gui.pages.TestRunViewPage;
import com.qaprosoft.zafira.tests.models.TestRunViewType;
import com.qaprosoft.zafira.tests.services.api.TestRunAPIService;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;
import com.qaprosoft.zafira.tests.services.gui.TestRunViewPageService;
import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

public class TestRunViewPageTest extends AbstractTest
{

	private static final String PROJECT_NAME = "UNKNOWN";

	private TestRunViewPage testRunViewPage;
	private TestRunViewPageService testRunViewPageService;
	private String testRunViewName;

	@Autowired
	private JobMapper jobMapper;

	@BeforeMethod
	public void setup()
	{
		this.testRunViewPage = new TestRunViewPage(driver);
		this.testRunViewPageService = new TestRunViewPageService(driver);
		LoginPage loginPage = new LoginPage(driver);
		LoginPageService loginPageService = new LoginPageService(driver);
		loginPage.open();
		DashboardPage dashboardPage = loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
		dashboardPage.waitUntilPageIsLoaded();
		this.testRunViewName = "test" + RandomUtils.nextInt(0, 10000);
	}

	@Test(groups = {"acceptance", "testRunView"}, enabled = false)
	public void verifyCreateTestRunViewTest()
	{
		DashboardPage dashboardPage = new DashboardPage(driver);
		CreateTestRunViewModalWindow createTestRunViewModalWindow = dashboardPage.getNavbar().goToCreateTestRunViewModal();
		Assert.assertEquals(createTestRunViewModalWindow.getHeaderText(), CreateTestRunViewModalWindow.TITLE, "Incorrect title of modal window");
		Assert.assertTrue(createTestRunViewModalWindow.hasDisabledAttribute(createTestRunViewModalWindow.getCreateButton()), "Create button is enabled");
		Assert.assertFalse(createTestRunViewModalWindow.isElementPresent(createTestRunViewModalWindow.getSaveButton(), 1), "Save button is present");
		Assert.assertFalse(
				createTestRunViewModalWindow.isElementPresent(createTestRunViewModalWindow.getDeleteButton(), 1), "Delete button is present");
		Assert.assertTrue(createTestRunViewModalWindow.getNameInput().getAttribute("value").isEmpty(), "Name input is not empty");
		createTestRunViewModalWindow.typeName(testRunViewName);
		Assert.assertTrue(createTestRunViewModalWindow.hasDisabledAttribute(createTestRunViewModalWindow.getCreateButton()), "Create button is enabled");
		createTestRunViewModalWindow.clearAllInputs();
		createTestRunViewModalWindow.selectProject(PROJECT_NAME);
		Assert.assertTrue(createTestRunViewModalWindow.hasDisabledAttribute(createTestRunViewModalWindow.getCreateButton()), "Create button is enabled");
		createTestRunViewModalWindow.typeName(testRunViewName);
		Assert.assertFalse(createTestRunViewModalWindow.hasDisabledAttribute(createTestRunViewModalWindow.getCreateButton()), "Create button is disabled");
		createTestRunViewModalWindow.clickCreateButton();
		dashboardPage.waitUntilElementWithTextIsPresent(dashboardPage.getSuccessAlert(), "View created successfully", 15);
		Assert.assertEquals(dashboardPage.getSuccessAlert().getText(), "View created successfully", "Alert text is incorrect");
		TestRunTabMenu testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		WebElement testRunView = testRunTabMenu.getTestRunsViewByName(testRunViewName);
		Assert.assertTrue(dashboardPage.isElementPresent(testRunView, 1), "Test run view is not present");
		Assert.assertTrue(dashboardPage.isElementPresent(testRunTabMenu.getTestRunViewEditIconByName(testRunViewName), 1), "Test run view edit icon is not present");
		testRunView.click();
		testRunViewPage.waitUntilPageIsLoaded();
		Assert.assertTrue(testRunViewPage.isOpened(), "Test run view page can not open");
	}

	@Test(groups = {"acceptance", "testRunView"})
	public void verifyUpdateTestRunViewTest()
	{
		testRunViewPageService.createTestRunView(this.testRunViewName, PROJECT_NAME);
		DashboardPage dashboardPage = new DashboardPage(driver);
		TestRunTabMenu testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		CreateTestRunViewModalWindow createTestRunViewModalWindow = testRunTabMenu.clickTestRunViewEditIconByName(this.testRunViewName);
		Assert.assertEquals(createTestRunViewModalWindow.getWebElementValue(createTestRunViewModalWindow.getNameInput()), this.testRunViewName, "Incorrect test run view name");
		Assert.assertEquals(createTestRunViewModalWindow.getSelectedValue(createTestRunViewModalWindow.getProjectSelect()), PROJECT_NAME, "Incorrect project selected value");
		Assert.assertFalse(
				createTestRunViewModalWindow.isElementPresent(createTestRunViewModalWindow.getCreateButton(), 1), "Create button is present");
		Assert.assertFalse(createTestRunViewModalWindow.hasDisabledAttribute(createTestRunViewModalWindow.getSaveButton()), "Save button is disabled");
		Assert.assertFalse(createTestRunViewModalWindow.hasDisabledAttribute(createTestRunViewModalWindow.getDeleteButton()), "Delete button is disabled");
		createTestRunViewModalWindow.clearAllInputs();
		Assert.assertTrue(createTestRunViewModalWindow.hasDisabledAttribute(createTestRunViewModalWindow.getSaveButton()), "Save button is not disabled");
		Assert.assertFalse(createTestRunViewModalWindow.hasDisabledAttribute(createTestRunViewModalWindow.getDeleteButton()), "Delete button is not disabled");
		this.testRunViewName = "test" + RandomUtils.nextInt(0, 10000);
		createTestRunViewModalWindow.typeName(this.testRunViewName);
		createTestRunViewModalWindow.selectProject(PROJECT_NAME);
		createTestRunViewModalWindow.clickSaveButton();
		dashboardPage.waitUntilElementWithTextIsPresent(dashboardPage.getSuccessAlert(), "View updated successfully", 5);
		Assert.assertEquals(dashboardPage.getSuccessAlert().getText(), "View updated successfully", "Success alert has incorrect text");
		dashboardPage.waitUntilElementIsNotPresent(dashboardPage.getSuccessAlert(), 5);
		testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		WebElement testRunView = testRunTabMenu.getTestRunsViewByName(this.testRunViewName);
		createTestRunViewModalWindow = testRunTabMenu.clickTestRunViewEditIconByName(this.testRunViewName);
		Assert.assertEquals(createTestRunViewModalWindow.getWebElementValue(createTestRunViewModalWindow.getNameInput()), this.testRunViewName, "Incorrect test run view name");
		Assert.assertEquals(createTestRunViewModalWindow.getSelectedValue(createTestRunViewModalWindow.getProjectSelect()), PROJECT_NAME, "Incorrect project selected value");
		createTestRunViewModalWindow.clickDeleteButton();
		dashboardPage.waitUntilElementWithTextIsPresent(dashboardPage.getSuccessAlert(), "View deleted successfully", 5);
		Assert.assertEquals(dashboardPage.getSuccessAlert().getText(), "View deleted successfully", "Incorrect alert text on delete");
		testRunTabMenu = dashboardPage.getNavbar().hoverOnTestRunTab();
		pause(1);
		Assert.assertFalse(testRunTabMenu.isElementPresent(testRunView, 1), "View is present after deleting");
	}

	@Test(groups = {"acceptance", "testRunView"})
	public void verifyViewTableCreationTest()
	{
		TestRunAPIService testRunAPIService = new TestRunAPIService();
		List<TestRunViewType> testRunViewTypes = testRunAPIService.createTestRuns(5,
				2, 2, 0, 2, 2, 101);
		testRunViewPageService.createTestRunView(this.testRunViewName, PROJECT_NAME);
		DashboardPage dashboardPage = new DashboardPage(driver);
		TestRunViewPage testRunViewPage = dashboardPage.getNavbar().goToTestRunView(this.testRunViewName);
		testRunViewPage.waitUntilPageIsLoaded();
		testRunViewPage.clickFabMenu();
		Assert.assertTrue(testRunViewPage.isElementPresent(testRunViewPage.getFabMenuButtonByClassName("plus"), 1), "Add fab button is not present");
		Assert.assertTrue(testRunViewPage.isElementPresent(testRunViewPage.getFabMenuButtonByClassName("undo"), 1), "Rebuild fab button is not present");
		testRunViewPage.clickOutside();
		JobViewSettingModalWindow jobViewSettingModalWindow = testRunViewPageService.goToCreateViewTableModalWindow();
		Assert.assertEquals(jobViewSettingModalWindow.getHeaderText(), JobViewSettingModalWindow.TITLE, "Modal window title is incorrect");
		Assert.assertTrue(jobViewSettingModalWindow.hasDisabledAttribute(jobViewSettingModalWindow.getCreateButton()), "Create button is enabled");
		jobViewSettingModalWindow.typeEnv("DEMO");
		jobViewSettingModalWindow.selectSize("1");
		jobViewSettingModalWindow.typePosition("2");
		jobViewSettingModalWindow.getJobViewSettingModalWindowFilterBoxesByNames(testRunViewTypes.stream()
				.map(type -> jobMapper.getJobById(type.getTestRunType().getJobId()).getName())
				.collect(Collectors.toList())).forEach(JobViewSettingModalWindowFilterBox::checkJobCheckbox);
		Assert.assertFalse(jobViewSettingModalWindow.hasDisabledAttribute(jobViewSettingModalWindow.getCreateButton()), "Create button is disabled");
		jobViewSettingModalWindow.clickCreateButton();
		testRunViewPage.waitUntilElementWithTextIsPresent(testRunViewPage.getSuccessAlert(), "Job view created successfully", 5);
		Assert.assertEquals(testRunViewPage.getSuccessAlert().getText(), "Job view created successfully", "Alert text is incorrect");
	}
}
