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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.qaprosoft.zafira.models.db.ScmAccount;
import com.qaprosoft.zafira.models.dto.ScmAccountType;
import com.qaprosoft.zafira.models.dto.scm.Organization;
import com.qaprosoft.zafira.models.dto.scm.Repository;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.scm.GitHubService;
import com.qaprosoft.zafira.services.services.application.scm.ScmAccountService;
import com.qaprosoft.zafira.ws.controller.AbstractController;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value = "SCM accounts API")
@CrossOrigin
@RequestMapping("api/scm")
public class ScmAPIController extends AbstractController {

    @Autowired
    private ScmAccountService scmAccountService;
    
    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Create SCM account", nickname = "createScmAccount", httpMethod = "POST", response = ScmAccountType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @RequestMapping(value="accounts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ScmAccountType createScmAccount(@Valid @RequestBody ScmAccountType scmAccountType) throws ServiceException {
        return mapper.map(scmAccountService.createScmAccount(mapper.map(scmAccountType, ScmAccount.class)), ScmAccountType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get SCM account by id", nickname = "getScmAccountById", httpMethod = "GET", response = ScmAccountType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "accounts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ScmAccountType getScmAccountById(@PathVariable(value = "id") Long id) throws ServiceException {
        return mapper.map(scmAccountService.getScmAccountById(id), ScmAccountType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all SCM accounts", nickname = "getAllScmAccounts", httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<ScmAccountType> getAllScmAccounts() throws ServiceException {
        return scmAccountService.getAllScmAccounts().stream().map(scmAccount -> mapper.map(scmAccount, ScmAccountType.class)).collect(Collectors.toList());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Update SCM account", nickname = "updateScmAccount", httpMethod = "PUT", response = ScmAccountType.class)
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="accounts", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ScmAccountType updateScmAccount(@RequestBody @Valid ScmAccountType scmAccountType) throws ServiceException {
        ScmAccount account = scmAccountService.getScmAccountById(scmAccountType.getId());
        if(account == null) {
            throw new ServiceException("Scm account with id " + scmAccountType.getId() + " does not exist.");
        }
        ScmAccount currentAccount = mapper.map(scmAccountType, ScmAccount.class);
        if(account.getUserId() == null || account.getUserId() <= 0) {
            currentAccount.setUserId(getPrincipalId());
        }
        return mapper.map(scmAccountService.updateScmAccount(currentAccount), ScmAccountType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Delete SCM account by id", nickname = "deleteScmAccountById", httpMethod = "DELETE")
    @ResponseStatus(HttpStatus.OK)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "accounts/{id}", method = RequestMethod.DELETE)
    public void deleteScmAccountById(@PathVariable(value = "id") Long id) throws ServiceException {
        scmAccountService.deleteScmAccountById(id);
    }
    
    @ResponseStatusDetails
    @ApiOperation(value = "Get client id", nickname = "getScmClientId", httpMethod = "GET", response = String.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "github/client", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getScmClientId(@RequestParam(value = "name", required = false) ScmAccount.Name name) {
        return gitHubService.getClientId();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Github callback", nickname = "callback", httpMethod = "GET", response = ScmAccountType.class)
    @RequestMapping(value = "github/exchange", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ScmAccountType authorizeCallback(@RequestParam(value = "code") String code) throws IOException, URISyntaxException, ServiceException {
        String accessToken = gitHubService.getAccessToken(code);
        if(StringUtils.isBlank(accessToken)) {
            throw new ForbiddenOperationException("Cannot recognize your authority");
        }
        return mapper.map(scmAccountService.createScmAccount(new ScmAccount(accessToken, ScmAccount.Name.GITHUB, getPrincipalId())), ScmAccountType.class);
    }
    
    @ResponseStatusDetails
    @ApiOperation(value = "Get all organizations", nickname = "getOrganizations", httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "github/organizations/{scmId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Organization> getOrganizations(@PathVariable(value = "scmId") Long id) throws IOException, ServiceException {
        ScmAccount scmAccount = this.scmAccountService.getScmAccountById(id);
        if(scmAccount == null) {
            throw new ForbiddenOperationException("Unable to list organizations");
        }
        return gitHubService.getOrganizations(scmAccount.getAccessToken());
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all repositories", nickname = "getRepositories", httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "github/repositories/{scmId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Repository> getRepositories(@PathVariable(value = "scmId") Long id, @RequestParam(value = "org", required = false) String organizationName) throws IOException, ServiceException {
        ScmAccount scmAccount = this.scmAccountService.getScmAccountById(id);
        if(scmAccount == null) {
            throw new ForbiddenOperationException("Unable to list repositories");
        }
        List<String> scmAccounts = scmAccountService.getAllScmAccounts().stream().map(ScmAccount::getRepositoryURL).collect(Collectors.toList());
        return gitHubService.getRepositories(this.scmAccountService.getScmAccountById(id).getAccessToken(), organizationName)
                .stream().filter(repository -> ! scmAccounts.contains(repository.getUrl())).collect(Collectors.toList());
    }
}