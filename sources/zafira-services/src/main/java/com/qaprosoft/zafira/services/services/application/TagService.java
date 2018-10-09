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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class TagService {

	@Autowired
	private TagMapper tagMapper;

	@Transactional(rollbackFor = Exception.class)
	public Tag createTag(Tag tag) {
		tagMapper.createTag(tag);
		return tag;
	}

	@Transactional(rollbackFor = Exception.class)
	public Set<Tag> createTags(Set<Tag> tags) {
		if(tags != null && tags.size() > 0) {
			tags.forEach(tag -> tagMapper.createTag(tag));
		}
		return tags;
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
	public Set<Tag> getAllTags() {
		return tagMapper.getAllTags();
	}

	@Transactional(readOnly = true)
	public Set<Tag> getTagsByTestId(Long testId) {
		return tagMapper.getTagsByTestId(testId);
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
