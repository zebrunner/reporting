package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.services.exceptions.ServiceException;
import freemarker.template.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Component
public class FreemarkerUtil
{

	private Logger LOGGER = Logger.getLogger(FreemarkerUtil.class);

	@Autowired
	private Configuration freemarkerConfiguration;

	public String getFreeMarkerTemplateContent(String template, Object obj) throws ServiceException
	{
		StringBuffer content = new StringBuffer();
		try
		{
			content.append(FreeMarkerTemplateUtils
					.processTemplateIntoString(freemarkerConfiguration.getTemplate(template), obj));
		} catch (Exception e)
		{
			LOGGER.error("Problem with free marker template compilation: " + e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return content.toString();
	}
}
