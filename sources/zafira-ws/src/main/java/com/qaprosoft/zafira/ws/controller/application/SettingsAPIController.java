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

package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.entity.integration.Integration;
import com.qaprosoft.zafira.services.services.application.ElasticsearchService;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.integration.IntegrationService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
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

import java.util.List;
import java.util.stream.Collectors;

@Api("Settings API")
@CrossOrigin
@RestController
@RequestMapping(path = "api/settings", produces = MediaType.APPLICATION_JSON_VALUE)
public class SettingsAPIController extends AbstractController {

    private final SettingsService settingsService;
    private final ElasticsearchService elasticsearchService;
    private final IntegrationService integrationService;

    public SettingsAPIController(
            SettingsService settingsService,
            ElasticsearchService elasticsearchService,
            IntegrationService integrationService
    ) {
        this.settingsService = settingsService;
        this.elasticsearchService = elasticsearchService;
        this.integrationService = integrationService;
    }

    /*@ResponseStatusDetails
    @ApiOperation(value = "Get settings by tool", nickname = "getSettingsByTool", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("tool/{tool}")
    public List<Setting> getSettingsByTool(@PathVariable("tool") String tool, @RequestParam(value = "decrypt", required = false) boolean decrypt) {
        return settingsService.getSettingsByTool(tool, decrypt);
    }*/

    @ResponseStatusDetails
    @ApiOperation(value = "Get settings by tool", nickname = "getSettingsByTool", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("tool/{tool}")
    public List<Setting> getSettingsByTool(@PathVariable("tool") String tool) {
        if (tool.equalsIgnoreCase("ELASTICSEARCH")) {
            return elasticsearchService.getSettings();
        } else if (tool.equalsIgnoreCase("RABBITMQ")) {
            Integration rabbit = integrationService.retrieveDefaultByIntegrationTypeName("RABBITMQ");
            return rabbit.getSettings()
                         .stream()
                         .map(setting -> new Setting(setting.getParam().getName(), setting.getValue()))
                         .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Unsupported tool, this API should not be used for anything but ElasticSearch");
        }
    }

    /*
    @ResponseStatusDetails
    @ApiOperation(value = "Get tools", nickname = "getTools", httpMethod = "GET", response = Map.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("tools")
    public Map<Tool, Boolean> getTools() {
        return settingsService.getToolsStatuses();
    }*/

    @ResponseStatusDetails
    @ApiOperation(value = "Update setting", nickname = "setting", httpMethod = "PUT", response = Setting.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public Setting updateSetting(@RequestBody Setting setting) {
        return settingsService.updateSetting(setting);
    }

    /*@ResponseStatusDetails
    @ApiOperation(value = "Update settings", nickname = "settings", httpMethod = "PUT", response = ConnectedToolType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('MODIFY_INTEGRATIONS')")
    @PutMapping("/tools")
    public ConnectedToolType updateSettings(@RequestBody List<Setting> settings) {
        return settingsService.updateSettings(settings);
    }

    @ApiOperation(value = "Upload setting file", nickname = "uploadSettingFile", httpMethod = "POST", response = ConnectedToolType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PostMapping("/tools")
    public ConnectedToolType uploadSettingFile(
            @RequestParam("tool") Tool tool,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return settingsService.createSettingFile(file.getBytes(), file.getOriginalFilename(), name, tool);
    }

    @ResponseStatusDetails
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @ApiOperation(value = "Is tool connected", nickname = "isToolConnected", httpMethod = "GET", response = Boolean.class)
    @GetMapping("tools/{name}")
    public Boolean isToolConnected(@PathVariable("name") Tool tool) {
        return settingsService.isConnected(tool);
    }*/

    @ResponseStatusDetails
    @ApiOperation(value = "Get company logo URL", nickname = "getSettingValue", httpMethod = "GET", response = Setting.class)
    @GetMapping("companyLogo")
    public Setting getCompanyLogoURL() {
        return settingsService.getSettingByName(Setting.SettingType.COMPANY_LOGO_URL.name());
    }

    /*@ResponseStatusDetails
    @ApiOperation(value = "Generate key", nickname = "generateKey", code = 201, httpMethod = "POST")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission('MODIFY_INTEGRATIONS')")
    @PostMapping("key/regenerate")
    public void reEncrypt() {
        settingsService.reEncrypt();
    }*/

}
