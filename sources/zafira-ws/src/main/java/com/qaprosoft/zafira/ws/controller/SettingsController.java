package com.qaprosoft.zafira.ws.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.SettingsService.SettingType;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("settings")
public class SettingsController extends AbstractController
{
	@Autowired
	private SettingsService settingsService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({"ROLE_ADMIN"})
	public @ResponseBody List<Setting> getAllSettings() throws ServiceException
	{
		return settingsService.getAllSettings();
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{name}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String getSettingValue(@PathVariable(value="name") String name) throws ServiceException
	{
		return settingsService.getSettingValue(SettingType.valueOf(name));
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({"ROLE_ADMIN"})
	public void deleteSetting(@PathVariable(value="id") long id) throws ServiceException
	{
		settingsService.deleteSettingById(id);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({"ROLE_ADMIN"})
	public @ResponseBody
	Setting createSetting(@RequestBody Setting setting) throws ServiceException
	{
		return settingsService.createSetting(setting);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({"ROLE_ADMIN"})
	public @ResponseBody
	Setting editSetting(@RequestBody Setting setting) throws ServiceException
	{
		return settingsService.updateSetting(setting);
	}
}
