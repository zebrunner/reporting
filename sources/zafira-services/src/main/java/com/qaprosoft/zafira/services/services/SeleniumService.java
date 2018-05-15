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
package com.qaprosoft.zafira.services.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.util.WebDriverUtil;

@Service
public class SeleniumService 
{
	private Logger LOGGER = Logger.getLogger(EmailService.class);
	
	private static final Dimension DEFAULT_SCREEN_DIMENSION = new Dimension(1920, 1080);
	
	private static final String MAC_CHROMEDRIVER = "classpath:chromedriver/chromedriver-macos";
	
	private static final String WINDOWS_CHROMEDRIVER = "classpath:chromedriver/chromedriver-windows.exe";
	
	private static final String LINUX_CHROMEDRIVER = "classpath:chromedriver/chromedriver-linux";

	private File binary;
	
	@Autowired
	private ResourceLoader resourceLoader;

     /**
	 * Initializes PhantomJS binary according to OS.
	 */
	@PostConstruct
	public void init() 
	{
		InputStream is = null;
		try
		{
			if (SystemUtils.IS_OS_MAC) 
			{
				is = resourceLoader.getResource(MAC_CHROMEDRIVER).getInputStream();
			} 
			else if (SystemUtils.IS_OS_WINDOWS) 
			{
				is = resourceLoader.getResource(WINDOWS_CHROMEDRIVER).getInputStream();
			} 
			else 
			{
				is = resourceLoader.getResource(LINUX_CHROMEDRIVER).getInputStream();
			}
			
			binary = new File("./chromedriver" + (SystemUtils.IS_OS_WINDOWS ? ".exe" : ""));

			//Files.deleteIfExists(binary.toPath());

			//System.setProperty("webdriver.chrome.driver", bin.getAbsolutePath());

			FileUtils.copyInputStreamToFile(is, binary);
			IOUtils.closeQuietly(is);
			
			Set<PosixFilePermission> perms = new HashSet<>();
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			perms.add(PosixFilePermission.OWNER_READ);
			Files.setPosixFilePermissions(binary.toPath(), perms);

		}
		catch(Exception e)
		{
			LOGGER.error("Failed to initialize PhantomJS: " + e.getMessage());
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
	}

	public List<Attachment> captureScreenshoots(List<String> urls, String domain, String auth, String projects, By areaLocator, By titleLocator, Dimension dimension) throws ServiceException
	{
		List<Attachment> attachments = new ArrayList<>();
		
		if(urls == null || StringUtils.isEmpty(auth))
		{
			throw new ServiceException("To capture screenshot specify: urls, auth");
		}
		
		WebDriver wd = null;
		try
		{
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setJavascriptEnabled(true);
			final ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--headless");
			chromeOptions.setBinary(binary);
			caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
	 		wd = new ChromeDriver(caps);
			
			wd.manage().window().setSize(dimension != null ? dimension : DEFAULT_SCREEN_DIMENSION);

			// Get first url to have a domain needed
			wd.get(urls.get(0));
			// Set cookies
			authorize(wd, auth, projects, domain, urls.get(0));
			// Get needed page 'cause login page delete all cookies
			wd.get(urls.get(0));
			// Refresh page to enable cookies
			wd.navigate().refresh();
			
			for(String url : urls)
			{
				wd.get(url);
				if(WebDriverUtil.isPageLoadingWithAnimation(wd) && WebDriverUtil.isPageLoading(wd))
				{
					WebDriverUtil.waitUntilPageIsLoaded(wd);
				} else
				{
					WebDriverUtil.pause(10, TimeUnit.SECONDS);
					WebDriverUtil.waitForJSandJQueryToLoad(wd);
				}

				hideElements(wd, wd.findElement(By.id("main-fab")));

				BufferedImage screenshot = WebDriverUtil.takeScreenShot(wd, wd.findElement(areaLocator));
				File screenshotDocument = saveImage(screenshot);

				String name = screenshotDocument.getName();

                if(titleLocator != null)
                {
                    name = wd.findElement(titleLocator).getAttribute("value");
                }

				attachments.add(new Attachment(name, screenshotDocument));
			}
		}
		catch(Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		finally
		{
			if(wd != null) wd.quit();
		}
		return attachments;
	}

	public void hideElements(WebDriver wd, WebElement... webElements)
	{
		JavascriptExecutor js = (JavascriptExecutor) wd;
		Arrays.asList(webElements).forEach(we -> js.executeScript("arguments[0].setAttribute('style', 'opacity:0')", we));
	}

	private File saveImage(final BufferedImage screenshot) throws IOException
	{
		String name = UUID.randomUUID().toString();
		File screenshotDocument = File.createTempFile(name, ".png");
		ImageIO.write(screenshot, "png", screenshotDocument);
		return screenshotDocument;
	}
	
	private String normalizeDomain(String domain)
	{
		if(!Pattern.matches("\\d+.\\d+.\\d+.\\d+", domain) && Pattern.matches("([A-z0-9-]+\\.)+[A-z0-9]+", domain))
		{
			String[] sd = domain.split("\\.");
			domain = "." + sd[sd.length - 2] + "." + sd[sd.length - 1];
		}
		return domain;
	}
	
	private void authorize(WebDriver wd, String auth, String projects, String domain, String url) throws InterruptedException
	{
		wd.manage().addCookie(new Cookie.Builder("Access-Token", auth).domain(normalizeDomain(domain)).build());
		wd.manage().addCookie(new Cookie.Builder("projects", projects).domain(normalizeDomain(domain)).build());
	}
}
