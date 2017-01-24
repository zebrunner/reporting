package com.qaprosoft.zafira.models.db.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "key", "value" })
public class Argument
{
	@XmlElement(required = true)
	protected String key;

	@XmlAttribute(name = "unique")
	protected boolean unique;

	@XmlElement(required = true)
	protected String value;
	
	public Argument()
	{
	}

	public Argument(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String value)
	{
		this.key = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public boolean getUnique()
	{
		return unique;
	}

	public void setUnique(boolean value)
	{
		this.unique = value;
	}
}