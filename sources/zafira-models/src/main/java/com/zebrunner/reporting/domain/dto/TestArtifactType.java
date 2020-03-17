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
package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TestArtifactType extends AbstractType {
    private static final long serialVersionUID = 555233394837989532L;

    private String name;
    private String link;
    private Long testId;
    private Integer expiresIn;

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof TestArtifactType) {
            equals = this.name.equals(((TestArtifactType) obj).getName());
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link);
    }
}