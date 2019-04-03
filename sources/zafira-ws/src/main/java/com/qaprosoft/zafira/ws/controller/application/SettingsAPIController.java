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
package com.qaprosoft.zafira.ws.controller.application;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.qaprosoft.zafira.services.services.application.jmx.google.GoogleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.Setting.Tool;
import com.qaprosoft.zafira.models.dto.ConnectedToolType;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.jmx.AmazonService;
import com.qaprosoft.zafira.services.services.application.jmx.CryptoService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
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
	private AmazonService amazonService;

	@Autowired
	private GoogleService googleService;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private CryptoService cryptoService;

	@Value("${zafira.amazon.token.expiration}")
	private Integer amazonTokenExpiration;

	@Value("${zafira.google.token.expiration}")
	private Long googleTokenExpiration;

	@ResponseStatusDetails
	@ApiOperation(value = "Get settings by tool", nickname = "getSettingsByTool", httpMethod = "GET", response = List.class)
	@ResponseStatus(HttpStatus.OK) @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@RequestMapping(value = "tool/{tool}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Setting> getSettingsByTool(@PathVariable(value="tool") String tool, @RequestParam(value = "decrypt", required = false, defaultValue="false") boolean decrypt) throws Exception
	{
		List<Setting> settings = settingsService.getSettingsByTool(Tool.valueOf(tool));
		
		if(decrypt) {
			// TODO: think about tools allowed for decryption
			if(!Tool.RABBITMQ.name().equals(tool)) {
				throw new ForbiddenOperationException();
			}
			for(Setting setting : settings) {
				if(setting.isEncrypted()) {
					setting.setValue(cryptoService.decrypt(setting.getValue()));
					setting.setEncrypted(false);
				}
			}
		}
		
        return settings;
	}

    @ResponseStatusDetails
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Get tools", nickname = "getTools", httpMethod = "GET", response = List.class)
    @RequestMapping(value = "tools", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Tool> getTools() throws ServiceException
    {
        return settingsService.getTools();
    }

	@ResponseStatusDetails
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@ApiOperation(value = "Is tool connected", nickname = "isToolConnected", httpMethod = "GET", response = Boolean.class)
	@RequestMapping(value = "tools/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Boolean isToolConnected(@PathVariable(value = "name") Tool tool) throws ServiceException
	{
		return settingsService.isConnected(tool);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Get company logo URL", nickname = "getSettingValue", httpMethod = "GET", response = Setting.class)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "companyLogo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Setting getCompanyLogoURL() throws ServiceException
	{
		return settingsService.getSettingByName(Setting.SettingType.COMPANY_LOGO_URL.name());
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Delete setting", nickname = "deleteSetting", httpMethod = "DELETE")
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_INTEGRATIONS')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteSetting(@PathVariable(value = "id") long id) throws ServiceException
	{
		settingsService.deleteSettingById(id);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Create setting", nickname = "createSetting", httpMethod = "POST", response = Setting.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_INTEGRATIONS')")
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Setting createSetting(@RequestBody Setting setting) throws Exception
	{
		return settingsService.createSetting(setting);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Edit setting", nickname = "editSetting", httpMethod = "PUT", response = Setting.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
	{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_INTEGRATIONS') or (#setting.name == 'COMPANY_LOGO_URL' and hasRole('ROLE_ADMIN'))")
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody void editSetting(@RequestBody Setting setting) throws Exception
	{
		settingsService.updateSetting(setting);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Edit settings", nickname = "editSettings", httpMethod = "PUT", response = List.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasPermission('MODIFY_INTEGRATIONS')")
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
        settingsService.notifyToolReinitiated(tool, TenancyContext.getTenantName());
        // TODO: find better solution
        TimeUnit.SECONDS.sleep(3);
		connectedTool.setName(tool.name());
		connectedTool.setSettingList(settings);
		connectedTool.setConnected(settingsService.getServiceByTool(tool).isEnabledAndConnected(tool));
        return connectedTool;
	}

	@ApiOperation(value = "Get amazon session credentials", nickname = "getSessionCredentials", httpMethod = "GET", response = SessionCredentials.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
	@RequestMapping(value = "amazon/creds", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody SessionCredentials getSessionCredentials() throws ServiceException
	{
		return amazonService.getTemporarySessionCredentials(amazonTokenExpiration);
	}

	@ApiOperation(value = "Get google session credentials", nickname = "getGoogleSessionCredentials", httpMethod = "GET", response = String.class)
	@ResponseStatus(HttpStatus.OK)
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
	@RequestMapping(value = "google/creds", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String getGoogleSessionCredentials() throws ServiceException, IOException {
		return googleService.getTemporaryAccessToken(googleTokenExpiration);
	}

	@ResponseStatusDetails
	@ApiOperation(value = "Generate key", nickname = "generateKey", code = 201, httpMethod = "POST")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiImplicitParams(
			{ @ApiImplicitParam(name = "Authorization", paramType = "header") })
	@PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
	@RequestMapping(value = "key/regenerate", method = RequestMethod.POST)
	public void reEncrypt() throws Exception
	{
		settingsService.reEncrypt();
	}
}
