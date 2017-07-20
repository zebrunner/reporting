package com.qaprosoft.zafira.services.services;

import java.util.List;

import com.qaprosoft.zafira.models.db.Dashboard;
import com.qaprosoft.zafira.services.util.crypto.CryptoTool;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.SettingsMapper;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class SettingsService
{
	@Autowired
	private SettingsMapper settingsMapper;

	private static CryptoTool cryptoTool = new CryptoTool();

	public enum SettingType
	{
		STF_NOTIFICATION_RECIPIENTS, JIRA_URL, JIRA_USER, JIRA_PASSWORD, JIRA_CLOSED_STATUS, SLACK_WEB_HOOK_URL, SLACK_NOTIF_CHANNEL_EXAMPLE;
	}

	@Transactional(readOnly = true)
	public Setting getSettingByName(String name) throws ServiceException
	{
		return settingsMapper.getSettingByName(name);
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
    public List<String> getTools() throws ServiceException
    {
        return settingsMapper.getTools();
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
	public Setting updateSetting(Setting setting) throws ServiceException
	{
		if (setting.isEncrypted() && !StringUtils.isEmpty(setting.getValue()))
		{
			setting.setValue(cryptoTool.encrypt(setting.getValue()));
		}
		settingsMapper.updateSetting(setting);
		return setting;
	}

	@Transactional(rollbackFor = Exception.class)
	public Setting createSetting(Setting setting) throws ServiceException
	{
		if (setting.isEncrypted() && !StringUtils.isEmpty(setting.getValue()))
		{
			setting.setValue(cryptoTool.encrypt(setting.getValue()));
		}
		settingsMapper.createSetting(setting);
		return setting;
	}
}
