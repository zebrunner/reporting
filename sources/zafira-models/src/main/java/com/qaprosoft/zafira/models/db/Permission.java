package com.qaprosoft.zafira.models.db;

public class Permission extends AbstractEntity implements Comparable<Permission>
{

	private static final long serialVersionUID = -3347361010220589543L;

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
		DASHBOARDS, TEST_RUNS, USERS, SETTINGS, MONITORS, PROJECTS, INTEGRATIONS
	}

	public enum Name
	{
		VIEW_HIDDEN_DASHBOARDS, MODIFY_DASHBOARDS,
		MODIFY_WIDGETS,
		MODIFY_TEST_RUN_VIEWS, MODIFY_TEST_RUNS, TEST_RUNS_CI,
		MODIFY_TESTS,
		MODIFY_USERS, VIEW_USERS, MODIFY_USER_GROUPS,
		MODIFY_SETTINGS, VIEW_SETTINGS,
		MODIFY_MONITORS, VIEW_MONITORS,
		MODIFY_PROJECTS,
		MODIFY_INTEGRATIONS, VIEW_INTEGRATIONS
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
