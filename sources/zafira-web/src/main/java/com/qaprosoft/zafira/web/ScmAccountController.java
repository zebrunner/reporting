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
package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.db.ScmAccount;
import com.qaprosoft.zafira.models.dto.ScmAccountType;
import com.qaprosoft.zafira.models.dto.scm.Organization;
import com.qaprosoft.zafira.models.dto.scm.Repository;
import com.qaprosoft.zafira.services.exceptions.ForbiddenOperationException;
import com.qaprosoft.zafira.services.services.application.scm.GitHubService;
import com.qaprosoft.zafira.services.services.application.scm.ScmAccountService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Api("SCM accounts API")
@CrossOrigin
@RequestMapping(path = "api/scm", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ScmAccountController extends AbstractController {

    private static final String ERR_MSG_SCM_ACCOUNT_NOT_FOUND = "SCM account with id %s can not be found";

    private final ScmAccountService scmAccountService;
    private final GitHubService gitHubService;
    private final Mapper mapper;

    public ScmAccountController(ScmAccountService scmAccountService, GitHubService gitHubService, Mapper mapper) {
        this.scmAccountService = scmAccountService;
        this.gitHubService = gitHubService;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create SCM account", nickname = "createScmAccount", httpMethod = "POST", response = ScmAccountType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @PostMapping("/accounts")
    public ScmAccountType createScmAccount(@Valid @RequestBody ScmAccountType scmAccountType) {
        return mapper.map(scmAccountService.createScmAccount(mapper.map(scmAccountType, ScmAccount.class)), ScmAccountType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get SCM account by id", nickname = "getScmAccountById", httpMethod = "GET", response = ScmAccountType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/accounts/{id}")
    public ScmAccountType getScmAccountById(@PathVariable("id") Long id) {
        return mapper.map(scmAccountService.getScmAccountById(id), ScmAccountType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all SCM accounts", nickname = "getAllScmAccounts", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/accounts")
    public List<ScmAccountType> getAllScmAccounts() {
        List<ScmAccount> scmAccounts = scmAccountService.getAllScmAccounts();
        return scmAccounts.stream()
                          .map(scmAccount -> mapper.map(scmAccount, ScmAccountType.class))
                          .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get default branch of SCM account by id", nickname = "getScmAccountDefaultBranch", httpMethod = "GET", response = String.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping(value = "/accounts/{id}/defaultBranch", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getScmAccountDefaultBranch(@PathVariable("id") long id) {
        return scmAccountService.getDefaultBranch(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update SCM account", nickname = "updateScmAccount", httpMethod = "PUT", response = ScmAccountType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @PutMapping("/accounts")
    public ScmAccountType updateScmAccount(@RequestBody @Valid ScmAccountType scmAccountType) {
        long scmAccountId = scmAccountType.getId();
        ScmAccount account = scmAccountService.getScmAccountById(scmAccountId);
        ScmAccount currentAccount = mapper.map(scmAccountType, ScmAccount.class);
        if (account.getUserId() == null || account.getUserId() <= 0) {
            currentAccount.setUserId(getPrincipalId());
        }
        return mapper.map(scmAccountService.updateScmAccount(currentAccount), ScmAccountType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Delete SCM account by id", nickname = "deleteScmAccountById", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/accounts/{id}")
    public void deleteScmAccountById(@PathVariable("id") Long id) {
        scmAccountService.deleteScmAccountById(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get client id", nickname = "getScmClientId", httpMethod = "GET", response = String.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @GetMapping(path = "github/client", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getScmClientId() {
        return gitHubService.getClientId();
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Github callback", nickname = "callback", httpMethod = "GET", response = ScmAccountType.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/github/exchange")
    public ScmAccountType authorizeCallback(@RequestParam("code") String code) throws IOException, URISyntaxException {
        String accessToken = gitHubService.getAccessToken(code);
        if (StringUtils.isBlank(accessToken)) {
            throw new ForbiddenOperationException("Cannot recognize your authority");
        }
        ScmAccount scmAccount = scmAccountService.createScmAccount(new ScmAccount(accessToken, ScmAccount.Name.GITHUB));
        return mapper.map(scmAccount, ScmAccountType.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all organizations", nickname = "getOrganizations", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/github/organizations/{scmId}")
    public List<Organization> getOrganizations(@PathVariable("scmId") Long id) throws IOException {
        ScmAccount scmAccount = scmAccountService.getScmAccountById(id);
        return gitHubService.getOrganizations(scmAccount);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all repositories", nickname = "getRepositories", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/github/repositories/{scmId}")
    public List<Repository> getRepositories(
            @PathVariable("scmId") Long id,
            @RequestParam(name = "org", required = false) String organizationName
    ) throws IOException {
        ScmAccount scmAccount = scmAccountService.getScmAccountById(id);
        List<ScmAccount> allAccounts = scmAccountService.getAllScmAccounts();
        List<String> repositoryUrls = allAccounts.stream()
                                                 .map(ScmAccount::getRepositoryURL)
                                                 .collect(Collectors.toList());

        List<Repository> repositories = gitHubService.getRepositories(scmAccount, organizationName);
        return repositories.stream()
                           .filter(repository -> !repositoryUrls.contains(repository.getUrl()))
                           .collect(Collectors.toList());
    }

}