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
package com.qaprosoft.zafira.models.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.qaprosoft.zafira.models.db.AbstractEntity;
import org.hibernate.validator.constraints.Email;
import com.qaprosoft.zafira.models.db.User;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvitationType extends AbstractEntity {

    private static final long serialVersionUID = 3259491531064546797L;

    @NotNull(message = "{error.email.required}")
    @Email(message = "{error.email.invalid}")
    private String email;

    @NotNull(message = "{error.source.required}")
    private User.Source source;

    @NotNull(message = "{error.group.required}")
    private Long groupId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public User.Source getSource() {
        return source;
    }

    public void setSource(User.Source source) {
        this.source = source;
    }

    @AssertTrue(message = "{error.email.invalid}")
    @JsonIgnore
    public boolean isEmailConfirmationValid() {
        return this.email == null || new EmailValidator().isValid(this.email, null);
    }
}
