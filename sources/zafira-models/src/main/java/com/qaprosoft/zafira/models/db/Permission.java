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
		READ_HIDDEN_DASHBOARD, WRITE_DASHBOARD,
		WRITE_WIDGET,
		WRITE_TEST_RUN_VIEW, WRITE_TEST_RUN, CI_TEST_RUN,
		WRITE_TEST,
		WRITE_USER, READ_USER, WRITE_USER_GROUP,
		WRITE_SETTING, READ_SETTING,
		WRITE_MONITOR, READ_MONITOR,
		WRITE_PROJECT,
		WRITE_INTEGRATION, READ_INTEGRATION
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
