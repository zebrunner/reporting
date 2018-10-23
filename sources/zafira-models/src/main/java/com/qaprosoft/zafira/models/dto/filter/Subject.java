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
package com.qaprosoft.zafira.models.dto.filter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Subject
{
	@NotNull(message = "Subject name required")
	private Name name;
	@Valid
	private List<Criteria> criterias;

	public enum Name
	{
		TEST_RUN
	}

	public Name getName()
	{
		return name;
	}

	public void setName(Name name)
	{
		this.name = name;
	}

	public List<Criteria> getCriterias()
	{
		return criterias;
	}

	public void setCriterias(List<Criteria> criterias)
	{
		this.criterias = criterias;
	}


	public void sortCriterias()
	{
		Collections.sort(this.criterias, Comparator.comparing(Criteria::getName));
	}
}
