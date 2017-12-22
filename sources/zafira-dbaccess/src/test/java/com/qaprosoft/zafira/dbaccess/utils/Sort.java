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
package com.qaprosoft.zafira.dbaccess.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.qaprosoft.zafira.models.db.AbstractEntity;

public class Sort<T extends AbstractEntity>
{
	public List<T> sortById(List<T> abstractEntityList)
	{
		Collections.sort(abstractEntityList, new Comparator<T>()
		{
			@Override
			public int compare(AbstractEntity o1, AbstractEntity o2)
			{
				return (int) (o1.getId() - o2.getId());
			}
		});
		return abstractEntityList;
	}
}