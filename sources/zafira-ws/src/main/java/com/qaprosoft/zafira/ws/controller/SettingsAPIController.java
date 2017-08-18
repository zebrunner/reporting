package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.tools.Tool;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Api(value = "Settings API")
@CrossOrigin
@RequestMapping("api/settings")
public class SettingsAPIController extends AbstractController
{

	private static final String ENCRYPTED_STRING = "******";

	@Autowired
	private SettingsService settingsService;

	@ResponseStatusDetails
	@ApiOperation(value = "Get all settings", nickname = "getAllSettings", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Setting> getAllSettings() throws ServiceException
	{
		return settingsService.getAllSettings();
	}


	@ResponseStatusDetails
	@ApiOperation(value = "Get settings by tool", nickname = "getSettingsByTool", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "tool/{tool}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Setting> getSettingsByTool(@PathVariable(value="tool") String tool) throws ServiceException
	{
		List<Setting> settings = settingsService.getSettingsByTool(tool);
		for(Setting setting : settings)
		{
			if(setting.isEncrypted() && ! StringUtils.isBlank(setting.getValue()))
			{
				setting.setValue(ENCRYPTED_STRING);
			}
		}
		return settings;
	}

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get tools", nickname = "getTools", code = 200, httpMethod = "GET", response = Map.class)
    @RequestMapping(value = "tools", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Boolean> getTools() throws ServiceException
    {
        return settingsService.getTools();
    }

	@ResponseStatusDetails
	@ApiOperation(value = "Get setting value", nickname = "getSettingValue", code = 200, httpMethod = "GET", response = String.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{name}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String getSettingValue(@PathVariable(value = "name") String name) throws ServiceException
	{
		return settingsService.getSettingValue(SettingsService.SettingType.valueOf(name));
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete setting", nickname = "deleteSetting", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteSetting(@PathVariable(value = "id") long id) throws ServiceException
	{
		settingsService.deleteSettingById(id);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create setting", nickname = "createSetting", code = 200, httpMethod = "POST", response = Setting.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Setting createSetting(@RequestBody Setting setting) throws Exception
	{
		return settingsService.createSetting(setting);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Edit setting", nickname = "editSetting", code = 200, httpMethod = "PUT", response = Setting.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Setting editSetting(@RequestBody Setting setting) throws Exception
	{
		return settingsService.updateSetting(setting);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Edit settings", nickname = "editSettings", code = 200, httpMethod = "PUT", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "tool", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<Tool, Boolean> editSettings(@RequestBody List<Setting> settings) throws Exception
	{
		return settingsService.updateToolSettings(settings);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Generate key", nickname = "generateKey", code = 201, httpMethod = "POST")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "key/regenerate", method = RequestMethod.POST)
	public void reEncrypt() throws Exception
	{
		settingsService.reEncrypt();
	}
}
