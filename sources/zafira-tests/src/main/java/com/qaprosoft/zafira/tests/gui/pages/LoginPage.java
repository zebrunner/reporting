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

public class LoginPage extends BasePage
{
	@FindBy(name="username")
	private WebElement usernameTextField;
	
	@FindBy(name="password")
	private WebElement passwordTextField;
	
	@FindBy(xpath="//button[@type='submit']")
	private WebElement loginButton;
	
	public LoginPage(WebDriver driver)
	{
		super(driver, "/signin");
	}

	public WebElement getUsernameTextField()
	{
		return usernameTextField;
	}

	public void typeUsername(String username)
	{
		usernameTextField.sendKeys(username);
		LOGGER.info("Type username: " + username);
	}

	public WebElement getPasswordTextField()
	{
		return passwordTextField;
	}

	public void typePassword(String password)
	{
		passwordTextField.sendKeys(password);
		LOGGER.info("Type password: " + password);
	}

	public WebElement getLoginButton()
	{
		return loginButton;
	}

	public void clickLoginButton()
	{
		loginButton.click();
		LOGGER.info("Login button was clicked");
	}
}