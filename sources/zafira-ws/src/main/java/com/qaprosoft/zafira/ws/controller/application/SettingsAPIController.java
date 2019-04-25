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
import java.util.Map;

import com.qaprosoft.zafira.services.services.application.integration.impl.google.GoogleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.Setting.Tool;
import com.qaprosoft.zafira.models.dto.ConnectedToolType;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.impl.AmazonService;
import com.qaprosoft.zafira.services.services.application.integration.impl.CryptoService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api("Settings API")
@CrossOrigin
@RestController
@RequestMapping(path = "api/settings", produces = MediaType.APPLICATION_JSON_VALUE)
public class SettingsAPIController extends AbstractController {

    private final AmazonService amazonService;
    private final GoogleService googleService;
    private final SettingsService settingsService;
    private final CryptoService cryptoService;
    private final Integer amazonTokenExpiration;
    private final Long googleTokenExpiration;

    public SettingsAPIController(AmazonService amazonService,
                                 GoogleService googleService,
                                 SettingsService settingsService,
                                 CryptoService cryptoService,
                                 @Value("${zafira.amazon.token.expiration}") Integer amazonTokenExpiration,
                                 @Value("${zafira.google.token.expiration}") Long googleTokenExpiration) {
        this.amazonService = amazonService;
        this.googleService = googleService;
        this.settingsService = settingsService;
        this.cryptoService = cryptoService;
        this.amazonTokenExpiration = amazonTokenExpiration;
        this.googleTokenExpiration = googleTokenExpiration;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get settings by tool", nickname = "getSettingsByTool", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("tool/{tool}")
    public List<Setting> getSettingsByTool(@PathVariable("tool") Tool tool, @RequestParam(value = "decrypt", required = false) boolean decrypt) throws Exception {
        List<Setting> settings = settingsService.getSettingsByTool(tool);

        if (decrypt) {
            if (!tool.isDecrypt()) {
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
    @ApiOperation(value = "Get tools", nickname = "getTools", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("tools")
    public Map<Tool, Boolean> getTools() {
        return settingsService.getToolsStatuses();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update settings", nickname = "settings", httpMethod = "PUT", response = ConnectedToolType.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasPermission('MODIFY_INTEGRATIONS')")
    @PutMapping("/tools")
    public ConnectedToolType updateSettings(@RequestBody List<Setting> settings) throws Exception {
        return settingsService.updateSettings(settings);
    }

    @ApiOperation(value = "Upload setting file", nickname = "uploadSettingFile", httpMethod = "POST", response = ConnectedToolType.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PostMapping(value = "/tools")
    public ConnectedToolType uploadSettingFile(@RequestParam("tool") Tool tool,
                                               @RequestParam("name") String name,
                                               @RequestParam("file") MultipartFile file) throws Exception {
        return settingsService.createSettingFile(file.getBytes(), file.getOriginalFilename(), name, tool);
    }

    @ResponseStatusDetails
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @ApiOperation(value = "Is tool connected", nickname = "isToolConnected", httpMethod = "GET", response = Boolean.class)
    @GetMapping("tools/{name}")
    public Boolean isToolConnected(@PathVariable(value = "name") Tool tool) throws ServiceException {
        return settingsService.isConnected(tool);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get company logo URL", nickname = "getSettingValue", httpMethod = "GET", response = Setting.class)
    @GetMapping("companyLogo")
    public Setting getCompanyLogoURL() throws ServiceException {
        return settingsService.getSettingByName(Setting.SettingType.COMPANY_LOGO_URL.name());
    }

    @ApiOperation(value = "Get amazon session credentials", nickname = "getSessionCredentials", httpMethod = "GET", response = SessionCredentials.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("amazon/creds")
    public SessionCredentials getSessionCredentials() throws ServiceException {
        return amazonService.getTemporarySessionCredentials(amazonTokenExpiration).orElse(null);
    }

    @ApiOperation(value = "Get google session credentials", nickname = "getGoogleSessionCredentials", httpMethod = "GET", response = String.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping(value = "google/creds", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getGoogleSessionCredentials() throws ServiceException, IOException {
        return googleService.getTemporaryAccessToken(googleTokenExpiration);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Generate key", nickname = "generateKey", code = 201, httpMethod = "POST")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PostMapping("key/regenerate")
    public void reEncrypt() throws Exception {
        settingsService.reEncrypt();
    }

}
