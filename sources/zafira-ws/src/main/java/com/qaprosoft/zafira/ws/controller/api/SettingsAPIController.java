package com.qaprosoft.zafira.ws.controller.api;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(value = "Settings API")
@CrossOrigin
@RequestMapping("api/settings")
public class SettingsAPIController extends AbstractController {

    @Autowired
    private SettingsService settingsService;

    @ResponseStatusDetails
    @ApiOperation(value = "Get all settings", nickname = "getAllSettings", code = 200, httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Setting> getAllSettings() throws ServiceException
    {
        return settingsService.getAllSettings();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get setting value", nickname = "getSettingValue", code = 200, httpMethod = "GET", response = String.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="{name}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getSettingValue(@PathVariable(value="name") String name) throws ServiceException
    {
        return settingsService.getSettingValue(SettingsService.SettingType.valueOf(name));
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete setting", nickname = "deleteSetting", code = 200, httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteSetting(@PathVariable(value="id") long id) throws ServiceException
    {
        settingsService.deleteSettingById(id);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create setting", nickname = "createSetting", code = 200, httpMethod = "POST", response = Setting.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Setting createSetting(@RequestBody Setting setting) throws ServiceException
    {
        return settingsService.createSetting(setting);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Edit setting", nickname = "editSetting", code = 200, httpMethod = "PUT", response = Setting.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Setting editSetting(@RequestBody Setting setting) throws ServiceException
    {
        return settingsService.updateSetting(setting);
    }
}
