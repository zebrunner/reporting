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
package com.qaprosoft.zafira.ws.controller.management;

import com.qaprosoft.zafira.models.dto.PermissionType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.management.MngPermissionService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Api(value = "Permissions management API")
@CrossOrigin
@RequestMapping("api/mng/permissions")
public class MngPermissionsAPIController {

    @Autowired
    private MngPermissionService mngPermissionService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Get all permissions", nickname = "getAllPermissions", code = 200, httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<PermissionType> getAllPermissions() throws ServiceException {
        return mngPermissionService.getAllPermissions().stream().map(permission -> mapper.map(permission, PermissionType.class))
                .collect(Collectors.toList());
    }
}
