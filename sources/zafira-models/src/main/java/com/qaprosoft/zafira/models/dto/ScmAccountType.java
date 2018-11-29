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
package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qaprosoft.zafira.models.db.ScmAccount;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScmAccountType extends AbstractType {

    private static final long serialVersionUID = 9120645976990419377L;

    private String organizationName;
    private String repositoryName;
    private Long userId;
    private ScmAccount.Name name;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ScmAccount.Name getName() {
        return name;
    }

    public void setName(ScmAccount.Name name) {
        this.name = name;
    }
}
