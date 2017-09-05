package com.qaprosoft.zafira.services.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qaprosoft.zafira.services.services.emails.AsynSendEmailTask;
import com.qaprosoft.zafira.services.services.jmx.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.SettingsMapper;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.Setting.SettingType;
import com.qaprosoft.zafira.models.db.Setting.Tool;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class SettingsService
{

	@Autowired
	private SettingsMapper settingsMapper;

    @Autowired
    private JiraService jiraService;

    @Autowired
    private JenkinsService jenkinsService;

    @Autowired
    private SlackService slackService;

    @Autowired
    private AsynSendEmailTask emailTask;

    @Autowired
    private AmazonService amazonService;

	@Autowired
	private CryptoService cryptoService;


	@Transactional(readOnly = true)
	public Setting getSettingByName(String name) throws ServiceException
	{
		return settingsMapper.getSettingByName(name);
	}

	@Transactional(readOnly = true)
	public Setting getSettingByType(SettingType type)
	{
        return settingsMapper.getSettingByName(type.name());
	}

	@Transactional(readOnly = true)
	public List<Setting> getSettingsByEncrypted(boolean isEncrypted) throws ServiceException
	{
		return settingsMapper.getSettingsByEncrypted(isEncrypted);
	}

	@Transactional(readOnly = true)
	public List<Setting> getSettingsByTool(Tool tool) throws ServiceException
	{
        return settingsMapper.getSettingsByTool(tool);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteSettingById(long id) throws ServiceException
	{
		settingsMapper.deleteSettingById(id);
	}

	@Transactional(readOnly = true)
	public List<Setting> getAllSettings() throws ServiceException
	{
		return settingsMapper.getAllSettings();
	}

	@Transactional(readOnly = true)
	public List<Setting> getSettingsByIntegration(boolean isIntegrationTool) throws ServiceException
	{
		return settingsMapper.getSettingsByIntegration(isIntegrationTool);
	}

    @Transactional(readOnly = true)
    public Map<Tool, Boolean> getTools() throws ServiceException
    {
    	Map<Tool, Boolean> tools = new HashMap<>();
    	Boolean value;
        for(Tool tool : settingsMapper.getTools()) {
        	if(tool != null && ! tool.equals(Tool.CRYPTO)) {
         		value = getServiceByTool(tool).isConnected();
				tools.put(tool, value);
			}
		}
		return tools;
    }

	@Transactional(readOnly = true)
	public String getSettingValue(Setting.SettingType type) throws ServiceException
	{
		return getSettingByName(type.name()).getValue();
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateSetting(Setting setting) throws Exception
	{
		settingsMapper.updateSetting(setting);
	}


	@Transactional(rollbackFor = Exception.class)
	public Setting createSetting(Setting setting) throws Exception
	{
		settingsMapper.createSetting(setting);
		return setting;
	}


	@Transactional(rollbackFor = Exception.class)
	public void reEncrypt() throws Exception {
		List<Setting> settings = getSettingsByEncrypted(true);
		for(Setting setting: settings){
			String decValue = cryptoService.decrypt(setting.getValue());
			setting.setValue(decValue);
		}
		cryptoService.generateKey();
        reinstantiateTool(Tool.CRYPTO);
		for(Setting setting: settings){
			String encValue = cryptoService.encrypt(setting.getValue());
			setting.setValue(encValue);
			updateSetting(setting);
		}
	}

	public void reinstantiateTool(Tool tool) {
		if (getServiceByTool(tool) != null)
		{
			getServiceByTool(tool).init();
		}
	}


	public IJMXService getServiceByTool(Tool tool) {
		IJMXService service = null;
		switch (tool) {
			case JIRA:
				service = jiraService;
				break;
			case JENKINS:
				service = jenkinsService;
				break;
			case SLACK:
				service = slackService;
				break;
			case CRYPTO:
				service = cryptoService;
				break;
			case EMAIL:
				service = emailTask;
				break;
			case AMAZON:
				service = amazonService;
				break;
			default:
				break;
		}
		return service;
	}

}
