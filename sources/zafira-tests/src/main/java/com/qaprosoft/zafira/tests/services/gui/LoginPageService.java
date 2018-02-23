package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.gui.AbstractPage;
import com.qaprosoft.zafira.tests.gui.pages.DashboardPage;
import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPageService extends AbstractPageService
{

	private LoginPage loginPage;

	public LoginPageService(WebDriver driver)
	{
		super(driver);
		this.loginPage = new LoginPage(driver);
	}

	public DashboardPage login(String username, String password)
	{
		loginPage.waitUntilLoadingContainerDisappears(10);
		loginPage.getUsernameTextField().sendKeys(username);
		loginPage.getPasswordTextField().sendKeys(password);
		loginPage.getLoginButton().click();
		return new DashboardPage(driver, GENERAL_DASHBOARD_ID);
	}

	public LoginPage logout()
	{
		DashboardPage dashboardPage = new DashboardPage(driver);
		dashboardPage.getHeader().logOut();
		return new LoginPage(driver);
	}

	public boolean isInvalidCredentials()
	{
		return driver.findElement(By.xpath("//p[text()='Invalid credentials']")).isDisplayed();
	}
}
