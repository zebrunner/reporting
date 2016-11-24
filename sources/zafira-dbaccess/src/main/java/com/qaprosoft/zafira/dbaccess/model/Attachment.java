package com.qaprosoft.zafira.dbaccess.model;

import java.io.File;

public class Attachment
{
	private String name;
	private File file;

	public Attachment()
	{
	}
	
	public Attachment(String name, File file)
	{
		this.name = name;
		this.file = file;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}
}