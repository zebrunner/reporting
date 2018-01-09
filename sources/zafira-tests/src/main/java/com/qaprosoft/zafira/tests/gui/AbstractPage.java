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

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.qaprosoft.zafira.tests.util.Config;

public abstract class AbstractPage
{
	protected static final Logger LOGGER = Logger.getLogger(AbstractPage.class);

	protected String url;
	
	protected WebDriver driver;

	public AbstractPage(WebDriver driver, String path)
	{
		this.driver = driver;
		this.url = Config.get("base_url") + path;
		PageFactory.initElements(driver, this);
	}
	
	public void open()
	{
		driver.get(url);
		driver.manage().window().maximize();
	}
	
	public boolean isOpened()
	{
		return new WebDriverWait(driver, 15).until(ExpectedConditions.urlMatches(url)).booleanValue();
	}
}
