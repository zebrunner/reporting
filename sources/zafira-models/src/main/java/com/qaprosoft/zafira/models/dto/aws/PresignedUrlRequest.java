/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.models.dto.aws;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class PresignedUrlRequest implements Serializable {

    private static final long serialVersionUID = 428009088818864325L;

    /**
     * Presigned URL expires in following number of seconds
     */
    @NotNull(message = "Expires in required")
    private Integer expiresIn;

    /**
     * Key to recognize file in bucket
     */
    @NotEmpty(message = "Key required")
    private String key;

    public PresignedUrlRequest() {
    }

    public PresignedUrlRequest(Integer expiresIn, String key) {
        this.expiresIn = expiresIn;
        this.key = key;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
