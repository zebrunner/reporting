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

import com.qaprosoft.zafira.models.db.management.Tenancy;
import com.qaprosoft.zafira.models.dto.management.TenancyType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.management.MngTenancyService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
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

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Api(value = "Tenancies management API")
@CrossOrigin
@RequestMapping("api/mng/tenancies")
public class MngTenanciesAPIController extends AbstractController {

    @Autowired
    private MngTenancyService mngTenancyService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Create tenancy", nickname = "createTenancy", code = 200, httpMethod = "POST", response = TenancyType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody TenancyType createTenancy(@Valid @RequestBody TenancyType tenancyType) throws ServiceException {
        return mapper.map(mngTenancyService.createTenancy(mapper.map(tenancyType, Tenancy.class)), TenancyType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get tenancy by id or name", nickname = "getTenancyByIdOrName", code = 200, httpMethod = "GET", response = TenancyType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "{idOrName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody TenancyType getTenancyByIdOrName(@PathVariable(value = "idOrName") String idOrName) throws ServiceException {
        return mapper.map(isNumber(idOrName) ? mngTenancyService.getTenancyById(Long.valueOf(idOrName)) : mngTenancyService.getTenancyByName(idOrName), TenancyType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all tenancies", nickname = "getAllTenancies", code = 200, httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<TenancyType> getAllTenancies() throws ServiceException {
        return mngTenancyService.getAllTenancies().stream().map(tenancy -> mapper.map(tenancy, TenancyType.class)).collect(Collectors.toList());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update tenancy", nickname = "updateTenancy", code = 200, httpMethod = "PUT", response = TenancyType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody TenancyType updateTenancy(@Valid @RequestBody TenancyType tenancyType) throws ServiceException {
        return mapper.map(mngTenancyService.updateTenancy(mapper.map(tenancyType, Tenancy.class)), TenancyType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete tenancy by id or name", nickname = "deleteTenancyByIdOrName", code = 200, httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams(
            { @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value = "{idOrName}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTenancyByIdOrName(@PathVariable(value = "idOrName") String idOrName) throws ServiceException {
        if(isNumber(idOrName)) {
            mngTenancyService.deleteTenancyById(Long.valueOf(idOrName));
        } else {
            mngTenancyService.deleteTenancyByName(idOrName);
        }
    }
}
