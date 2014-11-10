package com.qaprosoft.zafira.services.util;

import org.apache.commons.lang3.StringUtils;

public class URLFormatUtil
{
	public static String normalize(String url)
	{
		return (url != null) ? StringUtils.removeEnd(url, "/") : null;
	}
}
