package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.util.crypto.CryptoTool;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.qaprosoft.zafira.services.services.SettingsService.SettingType;
import org.springframework.stereotype.Service;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;

import javax.annotation.PostConstruct;

import java.util.List;

import static com.qaprosoft.zafira.services.services.SettingsService.SettingType.JIRA_CLOSED_STATUS;

@Service
public class JiraService
{
	private static final Logger LOGGER = Logger.getLogger(JiraService.class);

	private BasicCredentials credentials;
	
	private JiraClient jiraClient;

    @Autowired
    private CryptoTool cryptoTool;

    @Autowired
    private SettingsService settingsService;

    @PostConstruct
	public void getJiraInfo() throws ServiceException {
        String url = null;
        String username = null;
        String password = null;

        List<Setting> jiraSettings = settingsService.getSettingsByTool("JIRA");
		for (Setting setting : jiraSettings){
		    if (setting.isEncrypted() && !StringUtils.isEmpty(setting.getValue())){
                setting.setValue(cryptoTool.decrypt(setting.getValue()));
            }
            switch(setting.getName()){
		        case "JIRA_URL":
                    url = setting.getValue();
                    break;
                case "JIRA_USER":
                    username = setting.getValue();
                    break;
                case "JIRA_PASSWORD":
                    password = setting.getValue();
                    break;
            }
        }
		initJira(url, username, password);
	}

	public void initJira (String url, String username, String password){
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

	public boolean isConnected()
	{
		boolean connected = false;
		try
		{
			connected = this.jiraClient != null && this.jiraClient.getProjects() != null;
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
			issue = jiraClient.getIssue(ticket);
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
}