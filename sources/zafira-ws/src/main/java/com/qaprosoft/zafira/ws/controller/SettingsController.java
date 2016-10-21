package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.dbaccess.model.Setting;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.SettingsService.SettingType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Controller
@ApiIgnore
@RequestMapping("settings")
public class SettingsController extends AbstractController
{
	@Autowired
	private SettingsService settingsService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public ModelAndView openSettingsPage()
	{
		return new ModelAndView("settings/index");
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Setting> getAllSettings() throws ServiceException
	{
		return settingsService.getAllSettings();
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getSettingValue(@PathVariable(value="name") String name) throws ServiceException
	{
		return settingsService.getSettingValue(SettingType.valueOf(name));
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value="{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteSetting(@PathVariable(value="id") long id) throws ServiceException
	{
		settingsService.deleteSettingById(id);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Setting createSetting(@RequestBody Setting setting) throws ServiceException
	{
		return settingsService.createSetting(setting);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	Setting editSetting(@RequestBody Setting setting) throws ServiceException
	{
		return settingsService.updateSetting(setting);
	}
}
