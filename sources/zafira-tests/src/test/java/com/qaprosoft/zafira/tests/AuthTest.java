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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.qaprosoft.zafira.tests.gui.DashboardPage;
import com.qaprosoft.zafira.tests.gui.LoginPage;


public class AuthTest extends AbstractTest
{

	@Test(enabled=false, groups= {"acceptance", "auth"})
	public void testValidLogin()
	{
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		DashboardPage dashboardPage = loginPage.login(ADMIN1_USER, ADMIN1_PASS);
		Assert.assertTrue(dashboardPage.isOpened(), "Dashboard not opened!");
	}
	
	@Test(enabled=false, groups= {"acceptance", "auth"})
	public void testInvalidLogin()
	{
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		loginPage.login("invalid", "credentials");
		Assert.assertTrue(loginPage.isInvalidCredentials(), "Invalid credentials alert not showed!");
	}
}
