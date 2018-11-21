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
package com.qaprosoft.zafira.services.services.application;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.TagMapper;
import com.qaprosoft.zafira.models.db.Tag;
import com.qaprosoft.zafira.models.db.TestRailIntegrationInfo;
import com.qaprosoft.zafira.models.dto.tag.IntegrationTag;
import com.qaprosoft.zafira.models.dto.tag.TestCaseResult;
import com.qaprosoft.zafira.models.dto.tag.TestRailIntegrationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {

	@Autowired
	private TagMapper tagMapper;

	@Transactional(rollbackFor = Exception.class)
	public Tag createTag(Tag tag) {
		tagMapper.createTag(tag);
		if(tag.getId() == null || tag.getId() == 0) {
			Tag existsTag = getTagByNameAndValue(tag.getName(), tag.getValue());
			if(existsTag != null) {
				tag = existsTag;
			}
		}
		return tag;
	}

	@Transactional(rollbackFor = Exception.class)
	public Set<Tag> createTags(Set<Tag> tags) {
		Set<Tag> result = new HashSet<>();
		if(tags != null && tags.size() > 0) {
			result = tags.stream().map(this::createTag).collect(Collectors.toSet());
		}
		return result;
	}

	@Transactional(readOnly = true)
	public Tag getTagById(Long id) {
		return tagMapper.getTagById(id);
	}

	@Transactional(readOnly = true)
	public Tag getTagByNameAndTestId(String name, Long testId) {
		return tagMapper.getTagByNameAndTestId(name, testId);
	}

    @Transactional(readOnly = true)
    public List<TestRailIntegrationInfo> getTagsByNameAndTestRunCiRunId(IntegrationTag name, String ciRunId) {
        return tagMapper.getTagsByNameAndTestRunCiRunId(name, ciRunId);
    }

    @Transactional(readOnly = true)
	public Tag getTagByNameAndValue(String name, String value) {
		return tagMapper.getTagByNameAndValue(name, value);
	}

	@Transactional(readOnly = true)
	public Set<Tag> getAllTags() {
		return tagMapper.getAllTags();
	}

	@Transactional(readOnly = true)
	public Set<Tag> getTagsByTestId(Long testId) {
		return tagMapper.getTagsByTestId(testId);
	}

	@Transactional(readOnly = true)
	public void getTesRailIntegrationInfo(String ciRunId, TestRailIntegrationType integrationInfo) {
		List<TestRailIntegrationInfo> testRailIntegrationInfo = getTagsByNameAndTestRunCiRunId(IntegrationTag.TESTRAIL_TESTCASE_UUID, ciRunId);
		Map<String, TestCaseResult> testCaseResultMap = new HashMap<>();
		testRailIntegrationInfo.forEach (
				queryResult -> {
					String[] tagInfoArray = queryResult.getTagValue().split("-");
                    TestCaseResult testCaseResult;
                    List<String> defectList;
                    if(testCaseResultMap.get(tagInfoArray[2]) == null){
                        if(integrationInfo.getProjectId() == null){
                            integrationInfo.setProjectId(tagInfoArray[0]);
                            integrationInfo.setSuiteId(tagInfoArray[1]);
                        }
                        testCaseResult = new TestCaseResult();
                        testCaseResult.setTestCaseId(tagInfoArray[2]);
                        testCaseResult.setStatus(queryResult.getStatus());
                        defectList = new ArrayList<>();
                    } else {
                        testCaseResult = testCaseResultMap.get(tagInfoArray[2]);
                        defectList = testCaseResult.getDefects();
                    }
                    defectList.add(queryResult.getDefectId());
                    testCaseResult.setDefects(defectList);
                    testCaseResultMap.put(tagInfoArray[2], testCaseResult);
				}
		);
		integrationInfo.setTestCaseInfo((List<TestCaseResult>) testCaseResultMap.values());
	}

	@Transactional(readOnly = true)
	public Boolean isTagExistsByName(String name) {
		return tagMapper.isExists(name);
	}

	@Transactional(rollbackFor = Exception.class)
	public Tag updateTag(Tag tag) {
		tagMapper.updateTag(tag);
		return tag;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteTagById(Long id) {
		tagMapper.deleteTagById(id);
	}
}
