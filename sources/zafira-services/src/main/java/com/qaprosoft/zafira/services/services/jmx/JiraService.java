package com.qaprosoft.zafira.services.services.jmx;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.SettingsService;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.qaprosoft.zafira.models.db.tools.Tool.JIRA;
import static com.qaprosoft.zafira.services.services.SettingsService.SettingType.JIRA_CLOSED_STATUS;


@ManagedResource(objectName="bean:name=jiraService", description="Jira init Managed Bean",
		currencyTimeLimit=15, persistPolicy="OnUpdate", persistPeriod=200,
		persistLocation="foo", persistName="bar")
public class JiraService implements IJMXService
{
	private static final Logger LOGGER = Logger.getLogger(JiraService.class);

	private BasicCredentials credentials;
	
	private JiraClient jiraClient;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CryptoService cryptoService;

    @Override
	@PostConstruct
	public void init() {

        String url = null;
        String username = null;
        String password = null;

        try {
			List<Setting> jiraSettings = settingsService.getSettingsByTool(JIRA.name());
			for (Setting setting : jiraSettings)
			{
				if(settingsService.isSettingTypeEnumValid(setting.getName()))
				{
					if(setting.isEncrypted())
					{
						setting.setValue(cryptoService.decrypt(setting.getValue()));
					}
					switch (SettingsService.SettingType.valueOf(setting.getName()))
					{
						case JIRA_URL:
							url = setting.getValue();
							break;
						case JIRA_USER:
							username = setting.getValue();
							break;
						case JIRA_PASSWORD:
							password = setting.getValue();
							break;
					}
				}
			}
			init(url, username, password);
		} catch(Exception e) {
        	LOGGER.error("Setting does not exist", e);
		}
	}

	@ManagedOperation(description="Change Jira initialization")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "url", description = "Jira url"),
			@ManagedOperationParameter(name = "username", description = "Jira username"),
			@ManagedOperationParameter(name = "password", description = "Jira password")})
	public void init(String url, String username, String password){
		try
		{
			if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(username) && !StringUtils.isEmpty(password))
			{
				this.credentials = new BasicCredentials(username, password);
				this.jiraClient = new JiraClient(url, credentials);
             }
		} catch (Exception e)
		{
			LOGGER.error("Unable to initialize Jira integration: " + e.getMessage());
		}
	}

	@Override
	public boolean isConnected()
	{
		boolean connected = false;
		try
		{
			connected = getJiraClient() != null && getJiraClient().getProjects() != null;
		}
		catch(Exception e)
		{
			LOGGER.error("Unable to connect to JIRA", e);
		}
		return connected;
	}

	public Issue getIssue(String ticket)
	{
		Issue issue = null;
		try
		{
			issue = getJiraClient().getIssue(ticket);
		} catch (Exception e)
		{
			LOGGER.error("Unable to find Jira issue: " + ticket, e);
		}
		return issue;
	}

	public boolean isIssueClosed(String ticket) throws ServiceException {
		Issue issue = getIssue(ticket);
		return isIssueClosed(issue);
	}

	public boolean isIssueClosed(Issue issue) throws ServiceException {
		boolean isIssueClosed = false;
		String[] closeStatuses = settingsService.getSettingValue(JIRA_CLOSED_STATUS).split(";");
		for(String closeStatus: closeStatuses) {
			if(issue.getStatus().getName().equalsIgnoreCase(closeStatus)) {
				isIssueClosed = true;
			}
		}
		return isIssueClosed;
	}

	@ManagedAttribute(description="Get jira client")
	public JiraClient getJiraClient() {
		return jiraClient;
	}
}