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

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class JenkinsJobType implements Serializable {

    private static final long serialVersionUID = 4812574658258748772L;

    @NotEmpty(message = "{error.suite.required}")
    private String suite;

    @NotNull(message = "{error.scm.account.id.required}")
    @Min(value = 1, message = "{error.scm.account.id.invalid}")
    private Long scmAccountId;

    @NotEmpty(message = "{error.branch.required}")
    private String branch;

    private String[] args;

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public Long getScmAccountId() {
        return scmAccountId;
    }

    public void setScmAccountId(Long scmAccountId) {
        this.scmAccountId = scmAccountId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
