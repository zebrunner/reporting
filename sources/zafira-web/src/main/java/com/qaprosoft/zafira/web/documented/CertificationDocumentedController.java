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
            value = "Searches for certification info by upstream job details",
            notes = "Returns certification information (screenshots, platforms, correlation id), or null if an elasticsearch client is not initialized",
            nickname = "getCertificationDetails",
            httpMethod = "GET",
            response = CertificationType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "upstreamJobId", paramType = "query", dataTypeClass = Long.class, required = true, value = "The upstream job id"),
            @ApiImplicitParam(name = "upstreamJobBuildNumber", paramType = "query", dataTypeClass = Integer.class, required = true, value = "The build number of the upstream job")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns certification details", response = CertificationType.class),
            @ApiResponse(code = 404, message = "Indicates that the test run cannot be found during the details collection process", response = ErrorResponse.class)
    })
    CertificationType getCertificationDetails(Long upstreamJobId, Integer upstreamJobBuildNumber);

}
