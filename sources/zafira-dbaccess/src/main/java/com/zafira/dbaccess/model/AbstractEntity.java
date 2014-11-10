package com.zafira.dbaccess.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Transient;

/**
 * AbstractEntity - base for all domains.
 * 
 * @author Alex Khursevich
 */
public abstract class AbstractEntity implements Serializable
{
	private static final long serialVersionUID = 6187567312503626298L;

	private long id;
	@Transient
	private Date modifiedAt;
	@Transient
	private Date createdAt;

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public Date getModifiedAt()
	{
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt)
	{
		this.modifiedAt = modifiedAt;
	}

	public Date getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(Date createdAt)
	{
		this.createdAt = createdAt;
	}
}
