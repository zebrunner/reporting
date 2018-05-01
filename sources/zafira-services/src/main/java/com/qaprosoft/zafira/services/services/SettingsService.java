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

import java.util.List;
import java.util.stream.Collectors;

import com.qaprosoft.zafira.services.services.jmx.*;
import com.qaprosoft.zafira.services.services.jmx.ldap.LDAPService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qaprosoft.zafira.dbaccess.dao.mysql.SettingsMapper;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.Setting.SettingType;
import com.qaprosoft.zafira.models.db.Setting.Tool;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.emails.AsynSendEmailTask;

@Service
public class SettingsService
{
	@Autowired
	private SettingsMapper settingsMapper;

	@Autowired
	private JiraService jiraService;

	@Autowired
	private LDAPService ldapService;

	@Autowired
	private JenkinsService jenkinsService;

	@Autowired
	private SlackService slackService;

	@Autowired
	private AsynSendEmailTask emailTask;

	@Autowired
	private AmazonService amazonService;

	@Autowired
	private HipchatService hipchatService;

	@Autowired
	private CryptoService cryptoService;

	@Autowired
	private RabbitMQService rabbitMQService;

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

	public boolean isConnected(Tool tool)
	{
		return tool != null && !tool.equals(Tool.CRYPTO) && getServiceByTool(tool).isConnected();
	}

	@Transactional(readOnly = true)
	public List<Tool> getTools()
	{
		return settingsMapper.getTools().stream()
				.filter(tool -> tool != null && !tool.equals(Tool.CRYPTO))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public String getSettingValue(Setting.SettingType type) throws ServiceException
	{
		return getSettingByName(type.name()).getValue();
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateSetting(Setting setting) throws Exception
	{
		setting.setValue(StringUtils.isBlank(setting.getValue().trim()) ? null : setting.getValue());
		settingsMapper.updateSetting(setting);
	}

	@Transactional(rollbackFor = Exception.class)
	public Setting createSetting(Setting setting) throws Exception
	{
		settingsMapper.createSetting(setting);
		return setting;
	}

	@Transactional(rollbackFor = Exception.class)
	public void reEncrypt() throws Exception
	{
		List<Setting> settings = getSettingsByEncrypted(true);
		for (Setting setting : settings)
		{
			String decValue = cryptoService.decrypt(setting.getValue());
			setting.setValue(decValue);
		}
		cryptoService.generateKey();
		reinstantiateTool(Tool.CRYPTO);
		for (Setting setting : settings)
		{
			String encValue = cryptoService.encrypt(setting.getValue());
			setting.setValue(encValue);
			updateSetting(setting);
		}
	}

	public void reinstantiateTool(Tool tool)
	{
		if (getServiceByTool(tool) != null)
		{
			getServiceByTool(tool).init();
		}
	}

	public IJMXService getServiceByTool(Tool tool)
	{
		IJMXService service = null;
		switch (tool)
		{
		case LDAP:
			service = ldapService;
			break;
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
		case HIPCHAT:
			service = hipchatService;
			break;
		case RABBITMQ:
			service = rabbitMQService;
			break;
		default:
			break;
		}
		return service;
	}

}
