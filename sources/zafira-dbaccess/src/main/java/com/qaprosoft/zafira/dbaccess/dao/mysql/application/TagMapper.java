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
package com.qaprosoft.zafira.dbaccess.dao.mysql.application;

import com.qaprosoft.zafira.models.db.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface TagMapper {

	/**
	 * Creates tag if it is not exist
	 * @param tag - to create
	 */
	void createTag(Tag tag);

	/**
	 * Creates tags if it is not exist
	 * @param tags - to create
	 */
	void createTags(@Param(value = "tags") Set<Tag> tags);

	Tag getTagById(Long id);

	Tag getTagByNameAndTestId(@Param(value = "name") String name, @Param(value = "testId") Long testId);

	Tag getTagByNameAndValue(@Param(value = "name") String name, @Param(value = "value") String value);

	Set<Tag> getAllTags();

	Set<Tag> getTagsByTestId(Long testId);

	Boolean isExists(String name);

	void updateTag(Tag tag);

	void deleteTagById(Long id);
}
