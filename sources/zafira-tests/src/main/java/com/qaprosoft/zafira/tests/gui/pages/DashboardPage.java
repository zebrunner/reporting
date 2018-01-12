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
package com.qaprosoft.zafira.tests.gui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPage extends BasePage
{

	public DashboardPage(WebDriver driver)
	{
		super(driver, "/dashboards");
	}

	public DashboardPage(WebDriver driver, int dashboardId)
	{
		super(driver, String.format("/dashboards/%d", dashboardId));
	}

	@FindBy(xpath="//body[contains(@class, 'zaf-light')]")
	WebElement lightZafiraSchemaStyle;

	@FindBy(xpath="//body[contains(@class, 'zaf-dark')]")
	WebElement darkZafiraSchemaStyle;

	@FindBy(xpath="//md-radio-button[@value=32]")
	WebElement lightZafiraSchemaRadioButton;

	@FindBy(xpath="//md-radio-button[@value=22]")
	WebElement darkZafiraSchemaRadioButton;

	public UserProfilePage goToUserProfilePage()
	{
        this.waitUntilElementIsPresent(this.getHeader().getUserMenuButton(),2);
        this.getHeader().clickUserMenuButton().getUserProfileButton().click();
        return new UserProfilePage(driver);
	}

	public WebElement getLightZafiraSchemaStyle() {
		return lightZafiraSchemaStyle;
	}

	public WebElement getDarkZafiraSchemaStyle() {
		return darkZafiraSchemaStyle;
	}

	public WebElement getLightZafiraSchemaRadioButton() {
		return lightZafiraSchemaRadioButton;
	}

	public WebElement getDarkZafiraSchemaRadioButton() {
		return darkZafiraSchemaRadioButton;
	}
}