package com.qaprosoft.zafira.services.services;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.util.WebDriverUtil;

@Service
public class SeleniumService 
{
	private Logger LOGGER = Logger.getLogger(EmailService.class);
	
	private static final Dimension SCREEN_DIMENSION = new Dimension(1200, 800);
	
	private static final String MAC_PHANTOMJS = "classpath:phantomjs/phantomjs-macos";
	
	private static final String WINDOWS_PHANTOMJS = "classpath:phantomjs/phantomjs-windows.exe";
	
	private static final String LINUX_PHANTOMJS = "classpath:phantomjs/phantomjs-linux";
	
	@Autowired
	private ResourceLoader resourceLoader;

	@PostConstruct
	public void init() 
	{
		try
		{
			InputStream is = null;
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
	}

	public File captureScreenshoot(String url, String domain, String sessionId) throws ServiceException 
	{
		if(StringUtils.isEmpty(url) || StringUtils.isEmpty(domain) || StringUtils.isEmpty(sessionId))
		{
			throw new ServiceException("To capture screenshot specify: url, domain, sessionId");
		}
		
		WebDriver wd = null;
		File screenshot = null;
		try
		{
			wd = new PhantomJSDriver();
			wd.manage().window().setSize(SCREEN_DIMENSION);
			wd.manage().addCookie(new Cookie.Builder("JSESSIONID", sessionId).domain(normalizeDomain(domain)).build());
			wd.get(url);
			WebDriverUtil.pause(10, TimeUnit.SECONDS);
			WebDriverUtil.waitForJSandJQueryToLoad(wd);
			screenshot = ((TakesScreenshot) wd).getScreenshotAs(OutputType.FILE);
		}
		catch(Exception e)
		{
			LOGGER.error(e.getMessage());
		}
		finally
		{
			if(wd != null) wd.quit();
		}
		return screenshot;
	}
	
	private String normalizeDomain(String domain)
	{
		if(!Pattern.matches("\\d+.\\d+.\\d+.\\d+", domain) && Pattern.matches("[A-z0-9]+\\.[A-z0-9]+\\.[A-z0-9]+", domain))
		{
			String[] sd = domain.split("\\.");
			domain = "." + sd[sd.length - 2] + "." + sd[sd.length - 1];
		}
		return domain;
	}
}
