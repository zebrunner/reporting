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
package com.qaprosoft.zafira.models.db.management;

import com.qaprosoft.zafira.models.db.AbstractEntity;

public class Permission extends AbstractEntity implements Comparable<Permission>
{

	private static final long serialVersionUID = 8656404634145344599L;

	private Name name;
	private Block block;

	public Permission()
	{
	}

	public Permission(Name name)
	{
		this.name = name;
	}

	public enum Block
	{
	}

	public enum Name
	{
	}

	public Name getName()
	{
		return name;
	}

	public void setName(Name name)
	{
		this.name = name;
	}

	public Block getBlock()
	{
		return block;
	}

	public void setBlock(Block block)
	{
		this.block = block;
	}

	@Override
	public boolean equals(Object o)
	{
		boolean equals = false;
		if (o != null && o instanceof Permission)
		{
			if(this.getId() != null)
			{
				equals = this.getId().equals(((Permission) o).getId());
			}
			if(this.getName() != null)
			{
				equals = this.getName().name().equals(((Permission) o).getName().name());
			}
		}
		return equals;
	}

	@Override
	public int hashCode()
	{
		return this.getId() != null ? this.getId().intValue() : this.getName().name().hashCode();
	}

	@Override
	public int compareTo(Permission o)
	{
		return this.getId() > o.getId() ? 1 : -1;
	}
}
