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
package com.qaprosoft.zafira.tests.gui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPage extends AbstractPage
{

	@FindBy(xpath="//md-menu/button[descendant::img]")
	private WebElement userMenuButton;

	@FindBy(css="a[href$='profile']")
	private WebElement userProfileButton;

	public DashboardPage(WebDriver driver, int dashboardId)
	{
		super(driver, String.format("/dashboards/%d", dashboardId));
	}

	public UserProfilePage goToUserProfilePage()
	{
		userMenuButton.click();
		userProfileButton.click();
		UserProfilePage userProfilePage = new UserProfilePage(driver);
		userProfilePage.open();
		return userProfilePage;
	}



}