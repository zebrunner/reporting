package com.qaprosoft.zafira.services.services;

import com.qaprosoft.zafira.dbaccess.dao.mysql.SettingsMapper;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.tools.Tool;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.jmx.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private CryptoService cryptoService;

	public enum SettingType
	{
		STF_NOTIFICATION_RECIPIENTS,
		JIRA_URL, JIRA_USER, JIRA_PASSWORD, JIRA_CLOSED_STATUS, JIRA_ENABLED,
		JENKINS_URL, JENKINS_USER, JENKINS_PASSWORD, JENKINS_ENABLED,
		SLACK_WEB_HOOK_URL, SLACK_NOTIF_CHANNEL_EXAMPLE, CRYPTO_KEY_TYPE, CRYPTO_ALGORITHM, CRYPTO_KEY_SIZE,KEY
	}

	@Transactional(readOnly = true)
	public Setting getSettingByName(String name) throws ServiceException
	{
		Setting setting  = settingsMapper.getSettingByName(name);
		if (setting == null)
		{
			throw new ServiceException("Setting not found: " + name);
		}
		return setting;
	}

	@Transactional(readOnly = true)
	public List <Setting> getSettingsByEncrypted(boolean isEncrypted) throws ServiceException
	{
		List <Setting> settings  = settingsMapper.getSettingsByEncrypted(isEncrypted);
		if (settings.size() == 0)
		{
			throw new ServiceException("Settings not found: " + isEncrypted);
		}
		return settings;
	}
	
	@Transactional(readOnly = true)
	public Setting getSettingByName(SettingType type) throws ServiceException
	{
		return settingsMapper.getSettingByName(type.name());
	}

	@Transactional(readOnly = true)
	public List<Setting> getSettingsByTool(String tool) throws ServiceException
	{
	    List<Setting> settings = settingsMapper.getSettingsByTool(tool);
        if (settings.size() == 0)
        {
            throw new ServiceException("Settings not found for tool: " + tool);
        }
        return settings;
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
    public Map<String, Boolean> getTools() throws ServiceException
    {
    	Map<String, Boolean> tools = new HashMap<>();
    	Boolean value;
        for(String toolStr : settingsMapper.getTools()) {
        	value = null;
        	if(isToolEnumValid(toolStr)) {
        		Tool tool = Tool.valueOf(toolStr);
        		value = getServiceByTool(tool).isConnected();
			}
			tools.put(toolStr, value);
		}
		return tools;
    }

	@Transactional(readOnly = true)
	public String getSettingValue(SettingType type) throws ServiceException
	{
		Setting setting = getSettingByName(type.name());
		if (setting == null)
		{
			throw new ServiceException("Setting not found: " + type.name());
		}
		return setting.getValue();
	}

	@Transactional(rollbackFor = Exception.class)
	public Setting updateSetting(Setting setting) throws Exception
	{
        Setting dbSetting = getSettingByName(setting.getName());
        setting = encrypt(setting, dbSetting);
		settingsMapper.updateSetting(setting);
		reinstantiateTool(setting.getTool());
		return setting;
	}


	@Transactional(rollbackFor = Exception.class)
	public Map<Tool, Boolean> updateToolSettings(List<Setting> settings) throws Exception
	{
		Map<Tool, Boolean> tools = new HashMap<>();
		String tool = settings.get(0).getTool();
		for(Setting setting : settings) {
			Setting dbSetting = getSettingByName(setting.getName());
			setting = encrypt(setting, dbSetting);
			settingsMapper.updateSetting(setting);
		}
		reinstantiateTool(tool);
		tools.put(Tool.valueOf(tool), getServiceByTool(Tool.valueOf(tool)).isConnected());
		return tools;
	}

	@Transactional(rollbackFor = Exception.class)
	public Setting createSetting(Setting setting) throws Exception
	{
		if (setting.isEncrypted() && !StringUtils.isEmpty(setting.getValue()))
		{
			setting.setValue(cryptoService.encrypt(setting.getValue()));
		}
		settingsMapper.createSetting(setting);
		return setting;
	}

	@Transactional(rollbackFor = Exception.class)
	public void regenerateKey() throws Exception
	{
		cryptoService.generateKey();
	}

	@Transactional(rollbackFor = Exception.class)
	public Setting encrypt(Setting setting, Setting dbSetting) throws Exception {

	    if (!StringUtils.isEmpty(setting.getValue()))
		{
			if (setting.isEncrypted() && !dbSetting.isEncrypted())
			{
				setting.setValue(cryptoService.encrypt(setting.getValue()));
			}
			else if (!setting.isEncrypted() && dbSetting.isEncrypted())
			{
				setting.setValue(cryptoService.decrypt(setting.getValue()));
			}
		}
		else {
            setting.setEncrypted(false);
        }
		return setting;
	}

	@Transactional(rollbackFor = Exception.class)
	public void reEncrypt() throws Exception {
		List<Setting> settings = getSettingsByEncrypted(true);
		for(Setting setting: settings){
			String decValue = cryptoService.decrypt(setting.getValue());
			setting.setValue(decValue);
		}
        reinstantiateTool(Tool.CRYPTO.name());
		for(Setting setting: settings){
			String encValue = cryptoService.encrypt(setting.getValue());
			setting.setValue(encValue);
			updateSetting(setting);
		}

	}

	public void reinstantiateTool(String toolName) {
		if(isToolEnumValid(toolName)) {
			Tool tool = Tool.valueOf(toolName);
			if (getServiceByTool(tool) != null)
			{
                getServiceByTool(tool).init();
            }
		}
	}

	private boolean isToolEnumValid(String enumStr) {
		try {
			Tool.valueOf(enumStr);
			return true;
		} catch(Exception e) {
			return false;
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
			default:
				break;
		}
		return service;
	}
}
