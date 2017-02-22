package com.qaprosoft.zafira.services.util;

import org.apache.commons.lang.StringUtils;

public class URLFormatUtil
{
	public static String normalize(String url)
	{
		return (url != null) ? StringUtils.removeEnd(url, "/") : null;
	}
}
