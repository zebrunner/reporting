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

import com.qaprosoft.zafira.models.dto.CertificationType;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Certification API")
public interface CertificationDocumentedController {

    @ApiOperation(
            value = "Search certification info by upstream job details",
            notes = "Returns certification info (screenshots, platforms, correlation id) or null if elasticsearch client is not initialized",
            nickname = "getCertificationDetails",
            httpMethod = "GET",
            response = CertificationType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "Auth token (Bearer)"),
            @ApiImplicitParam(name = "upstreamJobId", paramType = "query", dataType = "number", required = true, value = "Upstream job id"),
            @ApiImplicitParam(name = "upstreamJobBuildNumber", paramType = "query", dataType = "number", required = true, value = "Upstream job build number")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns certification details", response = CertificationType.class),
            @ApiResponse(code = 404, message = "Indicates that test run can not be found during details collecting process", response = ErrorResponse.class)
    })
    CertificationType getCertificationDetails(Long upstreamJobId, Integer upstreamJobBuildNumber);

}
