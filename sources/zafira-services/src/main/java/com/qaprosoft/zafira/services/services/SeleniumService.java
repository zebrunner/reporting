package com.qaprosoft.zafira.services.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
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
	
	private static final String MAC_PHANTOMJS = "classpath:phantomjs/phantomjs-macos";
	
	private static final String WINDOWS_PHANTOMJS = "classpath:phantomjs/phantomjs-windows.exe";
	
	private static final String LINUX_PHANTOMJS = "classpath:phantomjs/phantomjs-linux";
	
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
				is = resourceLoader.getResource(MAC_PHANTOMJS).getInputStream();
			} 
			else if (SystemUtils.IS_OS_WINDOWS) 
			{
				is = resourceLoader.getResource(WINDOWS_PHANTOMJS).getInputStream();
			} 
			else 
			{
				is = resourceLoader.getResource(LINUX_PHANTOMJS).getInputStream();
			}
			
			File bin = new File("./phantomjs" + (SystemUtils.IS_OS_WINDOWS ? ".exe" : ""));
			FileUtils.copyInputStreamToFile(is, bin);
			IOUtils.closeQuietly(is);
			
			Set<PosixFilePermission> perms = new HashSet<>();
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			perms.add(PosixFilePermission.OWNER_READ);
			Files.setPosixFilePermissions(bin.toPath(), perms);
			
			System.setProperty("phantomjs.binary.path", bin.getAbsolutePath());
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

	public List<Attachment> captureScreenshoots(List<String> urls, String domain, String auth, By areaLocator, By titleLocator, Dimension dimension) throws ServiceException 
	{
		List<Attachment> attachments = new ArrayList<>();
		
		if(urls == null || StringUtils.isEmpty(auth))
		{
			throw new ServiceException("To capture screenshot specify: urls, auth");
		}
		
		WebDriver wd = null;
		try
		{
			wd = new PhantomJSDriver();
			
			wd.manage().window().setSize(dimension != null ? dimension : DEFAULT_SCREEN_DIMENSION);
			
			authorize(wd, auth, domain, urls.get(0));
			
			for(String url : urls)
			{
				wd.get(url);
				WebDriverUtil.pause(10, TimeUnit.SECONDS);
				WebDriverUtil.waitForJSandJQueryToLoad(wd);
				
				File screenshot = ((TakesScreenshot) wd).getScreenshotAs(OutputType.FILE);
				String name = screenshot.getName();
				
				if(titleLocator != null)
				{
					name = wd.findElement(titleLocator).getAttribute("value");
				}
				
				if(areaLocator != null)
				{
					cropRegion(wd, screenshot, areaLocator);
				}
				attachments.add(new Attachment(name, screenshot));
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
	
	private void cropRegion(WebDriver wd, File screenshot, By regionLocator) throws IOException
	{
		BufferedImage  imagr = ImageIO.read(screenshot);
		WebElement area = wd.findElement(regionLocator);
		Point point = area.getLocation();
		BufferedImage eleScreenshot= imagr.getSubimage(point.getX(), point.getY(), area.getSize().getWidth(), area.getSize().getHeight());
		ImageIO.write(eleScreenshot, "png", screenshot);
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
	
	private void authorize(WebDriver wd, String auth, String domain, String url) throws InterruptedException
	{
		wd.manage().addCookie(new Cookie.Builder("Access-Token", auth).domain(normalizeDomain(domain)).build());
	}
}
