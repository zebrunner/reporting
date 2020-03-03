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
package com.qaprosoft.zafira.service;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TagMapper;
import com.qaprosoft.zafira.models.db.Tag;
import com.qaprosoft.zafira.models.db.TagIntegrationData;
import com.qaprosoft.zafira.models.db.TestInfo;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Configuration;
import com.qaprosoft.zafira.models.dto.tag.IntegrationTag;
import com.qaprosoft.zafira.service.util.URLResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.service.util.XmlConfigurationUtil.readArguments;

@Service
public class TagService {

    private final TagMapper tagMapper;
    private final TestRunService testRunService;
    private final URLResolver urlResolver;

    public TagService(TagMapper tagMapper, TestRunService testRunService, URLResolver urlResolver) {
        this.tagMapper = tagMapper;
        this.testRunService = testRunService;
        this.urlResolver = urlResolver;
    }

    @Transactional(rollbackFor = Exception.class)
    public Tag createTag(Tag tag) {
        tagMapper.createTag(tag);
        if (tag.getId() == null || tag.getId() == 0) {
            Tag existingTag = getTagByNameAndValue(tag.getName(), tag.getValue());
            if (existingTag != null) {
                tag = existingTag;
            }
        }
        return tag;
    }

    @Transactional(rollbackFor = Exception.class)
    public Set<Tag> createTags(Set<Tag> tags) {
        Set<Tag> result = new HashSet<>();
        if (tags != null && !tags.isEmpty()) {
            result = tags.stream().map(this::createTag).collect(Collectors.toSet());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<TestInfo> getTestInfoByTagNameAndTestRunCiRunId(IntegrationTag tagName, String ciRunId) {
        return tagMapper.getTestInfoByTagNameAndTestRunCiRunId(tagName, ciRunId);
    }

    @Transactional(readOnly = true)
    public Tag getTagByNameAndValue(String name, String value) {
        return tagMapper.getTagByNameAndValue(name, value);
    }

    @Transactional(readOnly = true)
    public TagIntegrationData exportTagIntegrationData(String ciRunId, IntegrationTag tagName) {
        TestRun testRun = testRunService.getTestRunByCiRunIdFull(ciRunId);
        // ConfigXML parsing for TestRunName generation
        Configuration configuration = readArguments(testRun.getConfigXML());
        return TagIntegrationData.builder()
                                 .testRunName(testRun.getName())
                                 .testInfo(getTestInfoByTagNameAndTestRunCiRunId(tagName, ciRunId))
                                 .finishedAt(getFinishedAt(testRun))
                                 .startedAt(testRun.getStartedAt())
                                 .createdAfter(testRun.getCreatedAt())
                                 .env(testRun.getConfig().getEnv())
                                 .testRunId(testRun.getId().toString())
                                 .zafiraServiceUrl(urlResolver.buildWebURL())
                                 .customParams(getCustomParams(tagName, configuration))
                                 .build();
    }

    private Map<String, String> getCustomParams(IntegrationTag tagName, Configuration configuration) {
        Map<String, String> customParams = new HashMap<>();
        // IntegrationType-specific properties adding
        switch (tagName) {
            case TESTRAIL_TESTCASE_UUID:
                configuration.getArg().forEach(arg -> {
                    String key = arg.getKey();
                    String value = arg.getValue();
                    if (key.contains("testrail_assignee")) {
                        customParams.put("assignee", value);
                    } else if (key.contains("testrail_milestone")) {
                        customParams.put("milestone", value);
                    } else if (key.contains("testrail_run_name")) {
                        customParams.put("testrail_run_name", value);
                    }
                });
                break;
            case QTEST_TESTCASE_UUID:
                configuration.getArg().forEach(arg -> {
                    String key = arg.getKey();
                    String value = arg.getValue();
                    if (key.contains("qtest_cycle_name")) {
                        customParams.put("cycle_name", value);
                    }
                });
        }
        return customParams;
    }

    private Date getFinishedAt(TestRun testRun) {
        // finishedAt value generation based on startedAt & elapsed
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(testRun.getStartedAt());
        if (testRun.getElapsed() != null) {
            calendar.add(Calendar.SECOND, testRun.getElapsed());
        }
        return calendar.getTime();
    }

}
