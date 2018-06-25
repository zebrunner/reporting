package com.qaprosoft.zafira.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;

public class DashboardPageTest extends AbstractTest {

	@BeforeMethod
	public void loginUser() {
		LoginPageService loginPageService = new LoginPageService(driver);
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		loginPageService.login(ADMIN1_USER, ADMIN1_PASS);
	}

	@Test
	public void test() {

	}

}
