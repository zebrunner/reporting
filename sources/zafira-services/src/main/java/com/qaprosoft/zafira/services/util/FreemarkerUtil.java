/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.services.exceptions.ServiceException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.StringReader;
import java.util.UUID;

@Component
public class FreemarkerUtil
{

	private static final Logger LOGGER = Logger.getLogger(FreemarkerUtil.class);

	@Autowired
	private Configuration freemarkerConfiguration;

	public String getFreeMarkerTemplateContent(String template, Object obj) throws ServiceException
	{
		return getFreeMarkerTemplateContent(template, obj, true);
	}

	/**
	 * Precess template through freemarker engine
	 * @param template - path to template file .ftl or string template
	 * @param obj - object to process
	 * @param isPath - to recognize is template path to .ftl or is a prepared string
	 * @return processed template through freemarker engine
	 * @throws ServiceException - on freemarker template compilation
	 */
	public String getFreeMarkerTemplateContent(String template, Object obj, boolean isPath) throws ServiceException
	{
		StringBuilder content = new StringBuilder();
		try
		{
			Template fTemplate = isPath ? freemarkerConfiguration.getTemplate(template) : new Template(UUID.randomUUID().toString(),
				new StringReader(template), new Configuration(Configuration.VERSION_2_3_23));
			content.append(FreeMarkerTemplateUtils
					.processTemplateIntoString(fTemplate, obj));
		} catch (Exception e)
		{
			LOGGER.error("Problem with free marker template compilation: " + e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return content.toString();
	}
}
