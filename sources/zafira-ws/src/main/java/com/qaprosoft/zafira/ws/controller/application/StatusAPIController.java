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
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.services.exceptions.UnhealthyStateException;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RequestMapping(path = "api/status", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class StatusAPIController extends AbstractController {

    private final SettingsService settingsService;

    public StatusAPIController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get service status", nickname = "status", httpMethod = "GET", response = String.class)
    @GetMapping()
    public String getStatus() {
        try {
            final String version = settingsService.getPostgresVersion();
            if (StringUtils.isEmpty(version)) {
                throw new RuntimeException("Unable to retrieve Postgres version");
            }
        } catch (Exception e) {
            throw new UnhealthyStateException("Service has no DB connection");
        }
        return "Service is up and running";
    }

}