package com.qaprosoft.zafira.dbaccess.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qaprosoft.zafira.dbaccess.model.config.Argument;

@JsonInclude(Include.NON_NULL)
public class TestConfig extends AbstractEntity
{
	private static final long serialVersionUID = 5668009693004786533L;
	
	private String url;
	private String env;
	private String platform;
	private String platformVersion;
	private String browser;
	private String browserVersion;
	private String appVersion;
	private String locale;
	private String language;
	private String device;
	
	public TestConfig()
	{
	}
	
	public TestConfig(long id)
	{
		super.setId(id);
	}

	public TestConfig init(List<Argument> args)
	{
		for(Argument arg : args)
		{
			if("url".equals(arg.getKey()))
			{
				this.url = arg.getValue();
			}
			else if("env".equals(arg.getKey()))
			{
				this.env = arg.getValue();
			}
			else if("platform".equals(arg.getKey()))
			{
				this.platform = arg.getValue();
			}
			else if("platform_version".equals(arg.getKey()))
			{
				this.platformVersion = arg.getValue();
			}
			else if("browser".equals(arg.getKey()))
			{
				this.browser = arg.getValue();
			}
			else if("browser_version".equals(arg.getKey()))
			{
				this.browserVersion = arg.getValue();
			}
			else if("app_version".equals(arg.getKey()))
			{
				this.appVersion = arg.getValue();
			}
			else if("locale".equals(arg.getKey()))
			{
				this.locale = arg.getValue();
			}
			else if("language".equals(arg.getKey()))
			{
				this.language = arg.getValue();
			}
			else if("device".equals(arg.getKey()))
			{
				this.device = arg.getValue();
			}
		}
		return this;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getEnv()
	{
		return env;
	}

	public void setEnv(String env)
	{
		this.env = env;
	}

	public String getPlatform()
	{
		return platform;
	}

	public void setPlatform(String platform)
	{
		this.platform = platform;
	}

	public String getPlatformVersion()
	{
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion)
	{
		this.platformVersion = platformVersion;
	}

	public String getBrowser()
	{
		return browser;
	}

	public void setBrowser(String browser)
	{
		this.browser = browser;
	}

	public String getBrowserVersion()
	{
		return browserVersion;
	}

	public void setBrowserVersion(String browserVersion)
	{
		this.browserVersion = browserVersion;
	}

	public String getAppVersion()
	{
		return appVersion;
	}

	public void setAppVersion(String appVersion)
	{
		this.appVersion = appVersion;
	}

	public String getLocale()
	{
		return locale;
	}

	public void setLocale(String locale)
	{
		this.locale = locale;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public String getDevice()
	{
		return device;
	}

	public void setDevice(String device)
	{
		this.device = device;
	}
}
