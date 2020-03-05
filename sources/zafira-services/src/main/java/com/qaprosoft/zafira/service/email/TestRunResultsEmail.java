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
package com.qaprosoft.zafira.service.email;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.Test;
import com.qaprosoft.zafira.models.db.TestRun;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TestRunResultsEmail implements IEmailMessage {
    private static final String SUBJECT = "%s: %s";

    private Map<String, String> customValues = new HashMap<>();
    private TestRun testRun;
    private List<Test> tests;
    private String jiraURL;
    private boolean showOnlyFailures = false;
    private boolean showStacktrace = true;
    private int successRate;
    private String elapsed;

    public TestRunResultsEmail(TestRun testRun, List<Test> tests) {
        this.testRun = testRun;
        this.tests = tests;
        this.elapsed = testRun.getElapsed() != null ? LocalTime.ofSecondOfDay(testRun.getElapsed()).toString() : null;
    }

    @Override
    public String getSubject() {
        String status = buildStatusText(testRun);
        return String.format(SUBJECT, status, testRun.getName());
    }

    public static String buildStatusText(TestRun testRun) {
        return Status.PASSED.equals(testRun.getStatus()) && testRun.isKnownIssue() && !testRun.isBlocker() ? "PASSED (known issues)"
                : testRun.isBlocker() ? "FAILED (BLOCKERS)" : testRun.getStatus().name();
    }

    @Override
    public EmailType getType() {
        return EmailType.TEST_RUN;
    }

    @Override
    public List<Attachment> getAttachments() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

}
