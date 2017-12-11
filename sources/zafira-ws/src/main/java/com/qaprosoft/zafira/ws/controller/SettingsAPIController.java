package com.qaprosoft.zafira.ws.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.Setting.Tool;
import com.qaprosoft.zafira.models.dto.ConnectedToolType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.jmx.CryptoService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "Settings API")
@CrossOrigin
@RequestMapping("api/settings")
public class SettingsAPIController extends AbstractController
{

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private CryptoService cryptoService;

	@ResponseStatusDetails
	@ApiOperation(value = "Get all settings", nickname = "getAllSettings", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasAnyPermission('VIEW_SETTINGS', 'MODIFY_SETTINGS', 'VIEW_INTEGRATIONS', 'MODIFY_INTEGRATIONSS')")
	@RequestMapping(value = "list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Setting> getAllSettings() throws ServiceException
	{
        return settingsService.getAllSettings();
	}

    @ResponseStatusDetails
    @ApiOperation(value = "Get settings by integration", nickname = "getSettingsByIntegration", code = 200, httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasAnyPermission('VIEW_SETTINGS', 'MODIFY_SETTINGS', 'VIEW_INTEGRATIONS', 'MODIFY_INTEGRATIONSS')")
    @RequestMapping(value = "integration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Setting> getAllSettings(@RequestParam(value="isIntegrationTool", required = false) boolean isIntegrationTool) throws ServiceException
    {
        List<Setting> settings;
        if(isIntegrationTool)
        {
            settings = settingsService.getSettingsByIntegration(true);
        }
        else
        {
            settings = settingsService.getSettingsByIntegration(false);
        }
        return settings;
    }


	@ResponseStatusDetails
	@ApiOperation(value = "Get settings by tool", nickname = "getSettingsByTool", code = 200, httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "tool/{tool}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Setting> getSettingsByTool(@PathVariable(value="tool") String tool) throws ServiceException
	{
        return settingsService.getSettingsByTool(Tool.valueOf(tool));
	}

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get tools", nickname = "getTools", code = 200, httpMethod = "GET", response = Map.class)
    @RequestMapping(value = "tools", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<Tool, Boolean> getTools() throws ServiceException
    {
        return settingsService.getTools();
    }

	@ResponseStatusDetails
	@ApiOperation(value = "Get setting value", nickname = "getSettingValue", code = 200, httpMethod = "GET", response = String.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasAnyPermission('VIEW_SETTINGS', 'MODIFY_SETTINGS', 'VIEW_INTEGRATIONS', 'MODIFY_INTEGRATIONSS')")
	@RequestMapping(value = "{name}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String getSettingValue(@PathVariable(value = "name") String name) throws ServiceException
	{
		return settingsService.getSettingValue(Setting.SettingType.valueOf(name));
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete setting", nickname = "deleteSetting", code = 200, httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_SETTINGS')")
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
	@PreAuthorize("hasPermission('MODIFY_SETTINGS')")
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
	@PreAuthorize("hasPermission('MODIFY_SETTINGS')")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody void editSetting(@RequestBody Setting setting) throws Exception
	{
		settingsService.updateSetting(setting);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Edit settings", nickname = "editSettings", code = 200, httpMethod = "PUT", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_SETTINGS')")
	@RequestMapping(value = "tool", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ConnectedToolType editSettings(@RequestBody List<Setting> settings) throws Exception
	{
		ConnectedToolType connectedTool = new ConnectedToolType();
        Tool tool = settings.get(0).getTool();
        for(Setting setting : settings) {
			if (setting.isValueForEncrypting()) {
			    if(StringUtils.isBlank(setting.getValue())){
                    setting.setEncrypted(false);
                }
                else
                {
                    Setting dbSetting = settingsService.getSettingByName(setting.getName());
                    if(!setting.getValue().equals(dbSetting.getValue())){
                        setting.setValue(cryptoService.encrypt(setting.getValue()));
                        setting.setEncrypted(true);
                    }
                }
			}
            settingsService.updateSetting(setting);
		}
        settingsService.reinstantiateTool(tool);
		connectedTool.setName(tool.name());
		connectedTool.setSettingList(settings);
		connectedTool.setConnected(settingsService.getServiceByTool(tool).isConnected());
        return connectedTool;
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Generate key", nickname = "generateKey", code = 201, httpMethod = "POST")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_SETTINGS')")
	@RequestMapping(value = "key/regenerate", method = RequestMethod.POST)
	public void reEncrypt() throws Exception
	{
		settingsService.reEncrypt();
	}
}
