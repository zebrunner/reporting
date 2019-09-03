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
 ******************************************************************************/
package com.qaprosoft.zafira.models.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

import static com.qaprosoft.zafira.models.dto.filter.Operator.LAST_14_DAYS;
import static com.qaprosoft.zafira.models.dto.filter.Operator.LAST_24_HOURS;
import static com.qaprosoft.zafira.models.dto.filter.Operator.LAST_30_DAYS;
import static com.qaprosoft.zafira.models.dto.filter.Operator.LAST_7_DAYS;

@Getter
@Setter
public class Criteria {

    @NotNull(message = "Criteria name required")
    private Name name;

    @NotNull(message = "Operator required")
    private Operator operator;

    private List<Operator> operators;
    private String value;

    @JsonIgnore
    @AssertTrue(message = "Incorrect value")
    public boolean isValueNull() {
        return Arrays.asList(LAST_24_HOURS, LAST_7_DAYS, LAST_14_DAYS, LAST_30_DAYS).contains(this.operator) == (value == null);
    }

    public enum Name {
        STATUS,
        TEST_SUITE,
        JOB_URL,
        ENV,
        PLATFORM,
        DATE,
        PROJECT
    }
}
