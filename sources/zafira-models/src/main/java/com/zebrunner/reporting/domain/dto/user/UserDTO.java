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
package com.zebrunner.reporting.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zebrunner.reporting.domain.db.Permission;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.domain.db.UserPreference;
import com.zebrunner.reporting.domain.dto.AbstractType;
import com.zebrunner.reporting.domain.db.Group;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserDTO extends AbstractType {
    private static final long serialVersionUID = -6663692781158665080L;

    @NotEmpty(message = "Username required")
    @Pattern(regexp = "[\\w-]+", message = "Invalid format")
    private String username;

    private String email;

    @Size(min = 1, max = 100, message = "“First name should be 1 to 100 characters long")
    @Pattern(regexp = "^[A-Za-z0-9-.]+$", message = "First name should contain only letters, numbers, dashes and dots")
    private String firstName;

    @Size(min = 1, max = 100, message = "Last name should be 1 to 100 characters long")
    @Pattern(regexp = "^[A-Za-z0-9-.]+$", message = "Last name should contain only letters, numbers, dashes and dots")
    private String lastName;

    private String password;
    private String photoURL;
    private List<Group.Role> roles = new ArrayList<>();
    private Set<Permission> permissions = new HashSet<>();
    private List<UserPreference> preferences = new ArrayList<>();
    private Date lastLogin;
    private User.Source source;
    private User.Status status;

    public UserDTO(String username, String email, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @AssertTrue(message = "Email confirmation not matching")
    @JsonIgnore
    public boolean isEmailConfirmationValid() {
        return this.email == null || new EmailValidator().isValid(this.email, null);
    }

}