package com.qaprosoft.zafira.web.documented;

import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.UserPreferenceDTO;
import com.qaprosoft.zafira.models.dto.errors.ErrorResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Tests API")
public interface UserPreferenceDocumentedController {

    @ApiOperation(
            value = "Updates single user preference",
            notes = "Returns updated integration",
            nickname = "updatePreference",
            httpMethod = "PUT",
            response = TestRunType.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", paramType = "header", required = true, value = "The auth token (Bearer)"),
            @ApiImplicitParam(name = "id", paramType = "path", dataType = "number", required = true, value = "The path reference id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the updated user preference", response = TestRunType.class)
    })
    UserPreferenceDTO updatePreference(Long id, UserPreferenceDTO userPreferenceDTO);

}
