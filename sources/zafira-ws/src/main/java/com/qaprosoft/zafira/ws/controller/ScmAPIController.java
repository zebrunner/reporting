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
package com.qaprosoft.zafira.ws.controller;

import com.qaprosoft.zafira.models.db.ScmAccount;
import com.qaprosoft.zafira.models.dto.ScmAccountType;
import com.qaprosoft.zafira.models.dto.scm.Organization;
import com.qaprosoft.zafira.models.dto.scm.Repository;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.scm.GitHubService;
import com.qaprosoft.zafira.services.services.application.scm.IScmService;
import com.qaprosoft.zafira.services.services.application.scm.ScmAccountService;
import com.qaprosoft.zafira.ws.swagger.annotations.ResponseStatusDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Controller
@Api(value = "SCM API")
@CrossOrigin
@RequestMapping("api/scm")
public class ScmAPIController extends AbstractController {

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private ScmAccountService scmAccountService;

    @Autowired
    private Mapper mapper;

    @ResponseStatusDetails
    @ApiOperation(value = "Github callback", nickname = "callback", httpMethod = "GET", response = ScmAccountType.class)
    @RequestMapping(value = "authorized", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ScmAccountType authorizeCallback(@RequestParam(value = "code") String code) throws IOException, URISyntaxException, ServiceException {
        String accessToken = gitHubService.getAccessToken(code);
        if(StringUtils.isBlank(accessToken)) {
            throw new ForbiddenOperationException("Cannot recognize your authority");
        }
        return mapper.map(scmAccountService.createScmAccount(new ScmAccount(accessToken, ScmAccount.Name.GIT_HUB, getPrincipalId())), ScmAccountType.class);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get client id", nickname = "getScmClientId", httpMethod = "GET", response = String.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "client_id", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getScmClientId(@RequestParam(value = "name", required = false) ScmAccount.Name name) {
        return getService(name).getClientId();
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all repositories", nickname = "getRepositories", httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "repositories/{scmId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Repository> getRepositories(@PathVariable(value = "scmId") Long id, @RequestParam(value = "org", required = false) String organizationName,
                                                          @RequestParam(value = "name", required = false) ScmAccount.Name name) throws IOException, ServiceException {
        ScmAccount scmAccount = this.scmAccountService.getScmAccountById(id);
        if(scmAccount == null) {
            throw new ForbiddenOperationException("Unable to list repositories");
        }
        return getService(name).getRepositories(this.scmAccountService.getScmAccountById(id).getAccessToken(), organizationName);
    }

    @ResponseStatusDetails
    @ApiOperation(value = "Get all organizations", nickname = "getOrganizations", httpMethod = "GET", response = List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "organizations/{scmId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Organization> getOrganizations(@PathVariable(value = "scmId") Long id, @RequestParam(value = "name", required = false) ScmAccount.Name name) throws IOException, ServiceException {
        ScmAccount scmAccount = this.scmAccountService.getScmAccountById(id);
        if(scmAccount == null) {
            throw new ForbiddenOperationException("Unable to list organizations");
        }
        return getService(name).getOrganizations(this.scmAccountService.getScmAccountById(id).getAccessToken());
    }

    private IScmService getService(ScmAccount.Name name) {
        IScmService result = this.gitHubService;;
        if(name != null) {
            switch (name) {
                case GIT_HUB:
                    result = this.gitHubService;
                    break;
                default:
                    break;
            }
        }
        return result;
    }
}
