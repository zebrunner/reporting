package com.qaprosoft.zafira.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.SettingsMapper;
import com.qaprosoft.zafira.dbaccess.model.Setting;
import com.qaprosoft.zafira.services.exceptions.ServiceException;

@Service
public class SettingsService
{
	@Autowired
	private SettingsMapper settingsMapper;

	public enum SettingType
	{
		STF_NOTIFICATION_RECIPIENTS, JIRA_URL;
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
		settingsMapper.updateSetting(setting);
		return setting;
	}

	@Transactional(rollbackFor = Exception.class)
	public Setting createSetting(Setting setting) throws ServiceException
	{
		settingsMapper.createSetting(setting);
		return setting;
	}
}
