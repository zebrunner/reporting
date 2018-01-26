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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.qaprosoft.zafira.tests.util.Config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractPage extends AbstractUIObject
{

	protected String url;

	public AbstractPage(WebDriver driver, String path)
	{
		super(driver);
		this.url = Config.get("base_url") + path;
		PageFactory.initElements(driver, this);
	}
	
	public void open()
	{
		driver.get(url.replace("\\?", "?"));
		driver.manage().window().maximize();
	}
	
	public boolean isOpened()
	{
		return new WebDriverWait(driver, 15).until(ExpectedConditions.urlMatches(url)).booleanValue();
	}

	public String getUrl() {
		return url;
	}
}
