package com.qaprosoft.zafira.models.dto.aws;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadType
{

	private MultipartFile file;
	private Type type;

	public enum Type
	{
		USERS, COMMON
	}

	public FileUploadType()
	{
	}

	public FileUploadType(MultipartFile file, Type type)
	{
		this.file = file;
		this.type = type;
	}

	public MultipartFile getFile()
	{
		return file;
	}

	public void setFile(MultipartFile file)
	{
		this.file = file;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}
}
