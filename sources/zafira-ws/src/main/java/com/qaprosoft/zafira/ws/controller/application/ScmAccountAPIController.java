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
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.models.db.ScmAccount;
import com.qaprosoft.zafira.models.dto.ScmAccountType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.scm.ScmAccountService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Api(value = "SCM accounts API")
@CrossOrigin
@RequestMapping("api/scm/accounts")
public class ScmAccountAPIController extends AbstractController {

    @Autowired
    private ScmAccountService scmAccountService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Create SCM account", nickname = "createScmAccount", httpMethod = "POST", response = ScmAccountType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ScmAccountType createScmAccount(@Valid @RequestBody ScmAccountType scmAccountType) throws ServiceException {
        return mapper.map(scmAccountService.createScmAccount(mapper.map(scmAccountType, ScmAccount.class)), ScmAccountType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get SCM account by id", nickname = "getScmAccountById", httpMethod = "GET", response = ScmAccountType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ScmAccountType getScmAccountById(@PathVariable(value = "id") Long id) throws ServiceException {
        return mapper.map(scmAccountService.getScmAccountById(id), ScmAccountType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all SCM accounts", nickname = "getAllScmAccounts", httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<ScmAccountType> getAllScmAccounts() throws ServiceException {
        return scmAccountService.getAllScmAccounts().stream().map(scmAccount -> mapper.map(scmAccount, ScmAccountType.class)).collect(Collectors.toList());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update SCM account", nickname = "updateScmAccount", httpMethod = "PUT", response = ScmAccountType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ScmAccountType updateScmAccount(@RequestBody @Valid ScmAccountType scmAccountType) throws ServiceException {
        return mapper.map(scmAccountService.updateScmAccount(mapper.map(scmAccountType, ScmAccount.class)), ScmAccountType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete SCM account by id", nickname = "deleteScmAccountById", httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void deleteScmAccountById(@PathVariable(value = "id") Long id) throws ServiceException {
        scmAccountService.deleteScmAccountById(id);
    }
}
