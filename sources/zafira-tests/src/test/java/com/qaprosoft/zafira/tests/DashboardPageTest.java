package com.qaprosoft.zafira.tests;

import com.qaprosoft.zafira.services.util.WebDriverUtil;
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
		driver.get("http://echarts.baidu.com/echarts2/doc/example/line1.html#-en");
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
