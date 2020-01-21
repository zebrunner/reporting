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
package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.models.db.Setting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@Api("Settings API")
public interface SettingDocumentedController {

    @ApiOperation(
            value = "Retrieves default integration settings by integration type name",
            notes = "Returns found integration settings and decrypts encrypted settings. Works with 'ELASTICSEARCH', 'RABBITMQ' and 'ZEBRUNNER' types only.",
            nickname = "getSettingsByTool",
            httpMethod = "GET",
            response = List.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "tool", paramType = "path", dataType = "string", required = true, value = "Integration type name")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns found integration settings", response = List.class)
    })
    List<Setting> getSettingsByTool(String tool);

}
