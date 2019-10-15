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
package com.qaprosoft.zafira.models.push.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenancyResponseEventMessage extends EventMessage {

    private static final long serialVersionUID = -8149563995165621982L;

    private String token;
    private String zafiraURL;
    private Boolean success;
    private String message;

    public TenancyResponseEventMessage(String tenancy) {
        super(tenancy);
    }

    public TenancyResponseEventMessage(String tenancy, String token, String zafiraURL, Boolean success) {
        super(tenancy);
        this.token = token;
        this.zafiraURL = zafiraURL;
        this.success = success;
    }

}
