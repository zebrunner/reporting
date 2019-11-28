/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.service.SettingsService;
import com.qaprosoft.zafira.service.integration.tool.impl.StorageProviderService;
import com.qaprosoft.zafira.service.integration.tool.impl.google.GoogleService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Api("Settings API")
@CrossOrigin
@RestController
@RequestMapping(path = "api/settings", produces = MediaType.APPLICATION_JSON_VALUE)
public class SettingsController extends AbstractController {

    private final SettingsService settingsService;

    private final StorageProviderService storageProviderService;
    private final GoogleService googleService;

    public SettingsController(SettingsService settingsService, StorageProviderService storageProviderService, GoogleService googleService) {
        this.settingsService = settingsService;
        this.storageProviderService = storageProviderService;
        this.googleService = googleService;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get settings by tool", nickname = "getSettingsByTool", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("tool/{tool}")
    public List<Setting> getSettingsByTool(@PathVariable("tool") String tool) {
        // TODO by nsidorevich on 2019-10-09: refactor and remove
        return settingsService.getSystemSettings(tool);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update setting", nickname = "setting", httpMethod = "PUT", response = Setting.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public Setting updateSetting(@RequestBody Setting setting) {
        return settingsService.updateSetting(setting);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get company logo URL", nickname = "getSettingValue", httpMethod = "GET", response = Setting.class)
    @GetMapping("companyLogo")
    public Setting getCompanyLogoURL() {
        return settingsService.getSettingByName("COMPANY_LOGO_URL");
    }

    // TODO by nsidorevich on 2019-10-09: remove this crap

    @ApiOperation(value = "Get amazon session credentials", nickname = "getSessionCredentials", httpMethod = "GET", response = SessionCredentials.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("amazon/creds")
    public SessionCredentials getSessionCredentials() {
        return storageProviderService.getTemporarySessionCredentials().orElse(null);
    }

    @ApiOperation(value = "Get google session credentials", nickname = "getGoogleSessionCredentials", httpMethod = "GET", response = String.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping(path = "google/creds", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getGoogleSessionCredentials() throws IOException {
        return googleService.getTemporaryAccessToken();
    }

}
