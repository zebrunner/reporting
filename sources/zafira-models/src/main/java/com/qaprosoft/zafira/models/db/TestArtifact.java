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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class TestArtifact extends AbstractEntity implements Comparable<TestArtifact> {
    private static final long serialVersionUID = 2708440751800176584L;

    private String name;
    private String link;
    private Date expiresAt;
    private Long testId;

    public boolean isValid() {
        return name != null && !name.isEmpty() && link != null && !link.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TestArtifact that = (TestArtifact) o;

        if (!Objects.equals(name, that.name))
            return false;
        if (!Objects.equals(link, that.link))
            return false;
        if (!Objects.equals(expiresAt, that.expiresAt))
            return false;
        return Objects.equals(testId, that.testId);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (expiresAt != null ? expiresAt.hashCode() : 0);
        result = 31 * result + (testId != null ? testId.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(TestArtifact testArtifact) {
        String artifactName = testArtifact.getName();
        if ((name.equals("Log") || name.equals("Demo")) && !artifactName.equals("Log") && !artifactName.equals("Demo")) {
            return -1;
        } else if ((artifactName.equals("Log") || artifactName.equals("Demo")) && !name.equals("Log") && !name.equals("Demo")) {
            return 1;
        }
        return this.name.compareTo(testArtifact.getName());
    }

}