/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.tests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.qaprosoft.zafira.dbaccess.dao.mysql.UserMapper;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.qaprosoft.zafira.tests.util.Config;

@ContextConfiguration("classpath:dbaccess-test.xml")
public class AbstractTest extends AbstractTestNGSpringContextTests
{
	private Logger LOGGER = Logger.getLogger(AbstractTest.class);

	protected String ADMIN1_USER = Config.get("admin1.user");
	protected String ADMIN1_PASS = Config.get("admin1.pass");

	protected WebDriver driver;
	
	@BeforeTest
	public void start(ITestContext context) throws MalformedURLException
	{
		LOGGER.info(context.getCurrentXmlTest().getName() + " started");
		DesiredCapabilities dc = null;
		if("firefox".equalsIgnoreCase(Config.get("browser")))
		{
			dc = DesiredCapabilities.firefox();
		}
		else if("chrome".equalsIgnoreCase(Config.get("browser")))
		{
			dc = DesiredCapabilities.chrome();
		}
		else if("safari".equalsIgnoreCase(Config.get("browser")))
		{
			dc = DesiredCapabilities.safari();
		}
		driver = new RemoteWebDriver(new URL(Config.get("selenium_host")), dc);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	}
	
	@AfterTest
	public void shutdown(ITestContext context)
	{
		LOGGER.info(context.getCurrentXmlTest().getName() + " finished");
		driver.quit();
	}

	public void pause(double timeout)
	{
		try
		{
			Thread.sleep(new Double(timeout * 1000).intValue());
		} catch (InterruptedException e)
		{
			LOGGER.error(e.getMessage());
		}
	}
}
