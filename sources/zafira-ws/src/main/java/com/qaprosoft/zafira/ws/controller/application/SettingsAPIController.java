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

import com.qaprosoft.zafira.dbaccess.utils.TenancyContext;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.Setting.Tool;
import com.qaprosoft.zafira.models.dto.ConnectedToolType;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.services.services.application.integration.impl.AmazonService;
import com.qaprosoft.zafira.services.services.application.integration.impl.CryptoService;
import com.qaprosoft.zafira.services.services.application.integration.impl.google.GoogleService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Api("Settings API")
@CrossOrigin
@RequestMapping(path = "api/settings", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class SettingsAPIController extends AbstractController {

    @Autowired
    private AmazonService amazonService;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private CryptoService cryptoService;

    @Value("${zafira.amazon.token.expiration}")
    private Integer amazonTokenExpiration;

    @Value("${zafira.google.token.expiration}")
    private Long googleTokenExpiration;

    @ResponseStatusDetails
    @ApiOperation(value = "Get settings by tool", nickname = "getSettingsByTool", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/tool/{tool}")
    public List<Setting> getSettingsByTool(
            @PathVariable("tool") String tool,
            @RequestParam(value = "decrypt", required = false) boolean decrypt
    ) {
        List<Setting> settings = settingsService.getSettingsByTool(Tool.valueOf(tool));

        if (decrypt) {
            // TODO: think about tools allowed for decryption
            if (!Tool.RABBITMQ.name().equals(tool)) {
                throw new ForbiddenOperationException();
            }
            for (Setting setting : settings) {
                if (setting.isEncrypted()) {
                    setting.setValue(cryptoService.decrypt(setting.getValue()));
                    setting.setEncrypted(false);
                }
            }
        }

        return settings;
    }

    @ResponseStatusDetails
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Get tools", nickname = "getTools", httpMethod = "GET", response = List.class)
    @GetMapping("/tools")
    public List<Tool> getTools() throws ServiceException {
        return settingsService.getTools();
    }

    @ResponseStatusDetails
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Is tool connected", nickname = "isToolConnected", httpMethod = "GET", response = Boolean.class)
    @GetMapping("/tools/{name}")
    public Boolean isToolConnected(@PathVariable("name") Tool tool) throws ServiceException {
        return settingsService.isConnected(tool);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get company logo URL", nickname = "getSettingValue", httpMethod = "GET", response = Setting.class)
    @GetMapping("/companyLogo")
    public Setting getCompanyLogoURL() throws ServiceException {
        return settingsService.getSettingByName(Setting.SettingType.COMPANY_LOGO_URL.name());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete setting", nickname = "deleteSetting", httpMethod = "DELETE")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_INTEGRATIONS')")
    @DeleteMapping("/{id}")
    public void deleteSetting(@PathVariable("id") long id) throws ServiceException {
        settingsService.deleteSettingById(id);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Create setting", nickname = "createSetting", httpMethod = "POST", response = Setting.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_INTEGRATIONS')")
    @PostMapping()
    public Setting createSetting(@RequestBody Setting setting) throws Exception {
        return settingsService.createSetting(setting);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Edit setting", nickname = "editSetting", httpMethod = "PUT", response = Setting.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_INTEGRATIONS') or (#setting.name == 'COMPANY_LOGO_URL' and hasRole('ROLE_ADMIN'))")
    @PutMapping()
    public void editSetting(@RequestBody Setting setting) {
        settingsService.updateSetting(setting);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Edit settings", nickname = "editSettings", httpMethod = "PUT", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_INTEGRATIONS')")
    @PutMapping("/tool")
    public ConnectedToolType editSettings(@RequestBody List<Setting> settings) {
        ConnectedToolType connectedTool = new ConnectedToolType();
        Tool tool = settings.get(0).getTool();
        for (Setting setting : settings) {
            if (setting.isValueForEncrypting()) {
                if (StringUtils.isBlank(setting.getValue())) {
                    setting.setEncrypted(false);
                } else {
                    Setting dbSetting = settingsService.getSettingByName(setting.getName());
                    if (!setting.getValue().equals(dbSetting.getValue())) {
                        setting.setValue(cryptoService.encrypt(setting.getValue()));
                        setting.setEncrypted(true);
                    }
                }
            }
            settingsService.updateIntegrationSetting(setting);
        }
        settingsService.notifyToolReinitiated(tool, TenancyContext.getTenantName());
        connectedTool.setName(tool.name());
        connectedTool.setSettingList(settings);
        connectedTool.setConnected(integrationService.getServiceByTool(tool).isEnabledAndConnected());
        return connectedTool;
    }

    @ApiOperation(value = "Get amazon session credentials", nickname = "getSessionCredentials", httpMethod = "GET", response = SessionCredentials.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/amazon/creds")
    public SessionCredentials getSessionCredentials() throws ServiceException {
        return amazonService.getTemporarySessionCredentials(amazonTokenExpiration)
                            .orElse(null); // wtf, Bogdan?
    }

    @ApiOperation(value = "Get google session credentials", nickname = "getGoogleSessionCredentials", httpMethod = "GET", response = String.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping(path = "/google/creds", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getGoogleSessionCredentials() throws ServiceException, IOException {
        return googleService.getTemporaryAccessToken(googleTokenExpiration);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Generate key", nickname = "generateKey", code = 201, httpMethod = "POST")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PostMapping("/key/regenerate")
    public void reEncrypt() throws Exception {
        settingsService.reEncrypt();
    }
}
