package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.services.util.WebDriverUtil;
import org.openqa.selenium.Cookie;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.qaprosoft.zafira.tests.gui.pages.LoginPage;
import com.qaprosoft.zafira.tests.services.gui.LoginPageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DashboardPageTest extends AbstractTest {

	@BeforeMethod
	public void loginUser() {
		driver.get("http://localhost:3000/#!/dashboards/10");
		driver.manage().addCookie(new Cookie("Access-Token", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwidXNlcm5hbWUiOiJhZG1pbiIsImdyb3VwSWRzIjpbM10sInRlbmFudCI6InN0YWdlIiwiZXhwIjoxNTQyMTE2NDUyfQ.je0kTz-301swQOnLhAr_8JOVb5emToGERf_2Byws6BnFBQil6sNbvGvF2EODNiI_ZD9UBUBYDSp5-MfknxE45Q"));
		driver.get("http://localhost:3000/#!/dashboards/10");
		/*LoginPageService loginPageService = new LoginPageService(driver);
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		loginPageService.login(ADMIN1_USER, ADMIN1_PASS);*/
	}

	@Test
	public void test() {
		pause(5);
		BufferedImage result = WebDriverUtil.takeScreenShot(driver, "body");
		try {
			ImageIO.write(result, "png", new File("/Users/brutskov/git/zafira/sources/zafira-services/src/main/resources/test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
