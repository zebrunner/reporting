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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.qaprosoft.zafira.tests.util.Config;

public class AbstractTest
{
	protected String ADMIN1_USER = Config.get("admin1.user");
	protected String ADMIN1_PASS = Config.get("admin1.pass");
	
	protected WebDriver driver;
	
	@BeforeTest
	public void start() throws MalformedURLException
	{
		DesiredCapabilities dc = null;
		if("firefox".equalsIgnoreCase(Config.get("browser")))
		{
			dc = DesiredCapabilities.firefox();
		}
		if("chrome".equalsIgnoreCase(Config.get("browser")))
		{
			dc = DesiredCapabilities.chrome();
		}
		driver = new RemoteWebDriver(new URL(Config.get("selenium_host")), dc);
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	}
	
	@AfterTest
	public void shutdown()
	{
		driver.quit();
	}
}
