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
package com.qaprosoft.zafira.models.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Invitation extends AbstractEntity {

    private static final long serialVersionUID = -7507603908818483927L;

    private String email;
    private String token;
    private User createdBy;
    private Status status;
    private User.Source source;
    private Long groupId;
    private String url;

    public enum Status {
        PENDING,
        ACCEPTED
    }

    public Invitation(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public boolean isValid() {
        return this.status != null && this.getStatus().equals(Status.PENDING);
    }

}
