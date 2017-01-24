package com.qaprosoft.zafira.models.dto.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "arg" })
@XmlRootElement(name = "config")
public class ConfigurationType
{
	protected List<ArgumentType> arg;

	public List<ArgumentType> getArg()
	{
		if (arg == null)
		{
			arg = new ArrayList<ArgumentType>();
		}
		return this.arg;
	}
}