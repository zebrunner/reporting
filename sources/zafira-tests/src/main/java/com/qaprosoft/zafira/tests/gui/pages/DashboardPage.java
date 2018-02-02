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
	@FindBy(xpath="//button[descendant::span[contains(@class, 'fa-bars')]]")
	private WebElement menuButton;

	@FindBy(xpath="//button[descendant::span[contains(@class, 'fa-plus')]]")
	private WebElement newDashboardButton;

	@FindBy(xpath="//button[descendant::span[contains(@class, 'fa-cog')]]")
	private WebElement updateDashboardButton;

	@FindBy(xpath="//button[descendant::span[contains(@class, 'fa-envelope-o')]]")
	private WebElement sendDashboardByEmailButton;

	@FindBy(xpath="//button[descendant::span[contains(@class, 'fa-arrows-alt')]]")
	private WebElement updateWidgetsButton;

	@FindBy(xpath="//button[descendant::span[contains(@class, 'fa-trash')]]")
	private WebElement deleteDashboardButton;


	/*public UserMenu clickUserMenuButton()
	{
		if(! isElementClickable(userMenuButton, 2) || isElementPresent(getBackdrop(), 1))
			clickOutside();
		waitUntilElementToBeClickableByBackdropMask(this.userMenuButton, 2);
		this.userMenuButton.click();
		return new UserMenu(driver, null);
	}
*/
	public DashboardPage(WebDriver driver)
	{
		super(driver, "/dashboards");
	}

	public DashboardPage(WebDriver driver, int dashboardId)
	{
		super(driver, String.format("/dashboards/%d", dashboardId));
	}
}