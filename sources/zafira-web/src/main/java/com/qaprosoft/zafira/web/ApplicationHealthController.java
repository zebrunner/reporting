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
 *******************************************************************************/
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.dbaccess.persistence.IntegrationRepository;
import com.qaprosoft.zafira.service.SettingsService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RequestMapping(path = "api/status", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ApplicationHealthController extends AbstractController {

    private final SettingsService settingsService;

    @Autowired
    private IntegrationRepository integrationRepository;

    public ApplicationHealthController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get service status", nickname = "status", httpMethod = "GET", response = String.class)
    @GetMapping()
    public String getStatus() {
        String version = settingsService.getPostgresVersion();
        if (StringUtils.isEmpty(version)) {
            throw new RuntimeException("Unable to retrieve Postgres version");
        }
        return "Service is up and running. Integration: " + integrationRepository.findById(1L).orElse(null);
    }

}