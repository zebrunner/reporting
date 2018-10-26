/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.ws.controller.application;

import com.qaprosoft.zafira.ws.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.models.dto.CertificationType;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.application.TestConfigService;
import com.qaprosoft.zafira.services.services.application.TestRunService;
import com.qaprosoft.zafira.services.services.application.jmx.amazon.AmazonService;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("api/certification")
public class CertificationAPIController extends AbstractController
{
	@Autowired
	private TestRunService testRunService;
	
	@Autowired
	private AmazonService amazonService;
	
	@Autowired
	private TestConfigService testConfigService;

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(path="details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody CertificationType getCertifcationDetails(@RequestParam(value="upstreamJobId", required=true) Long upstreamJobId, @RequestParam(value="upstreamJobBuildNumber", required=true) Integer upstreamJobBuildNumber) throws ServiceException
	{
		CertificationType certification = new CertificationType();
		
		for(TestRun testRun : testRunService.getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(upstreamJobId, upstreamJobBuildNumber))
		{
			String platform = testRun.getPlatform();
			for(Argument arg : testConfigService.readConfigArgs(testRun.getConfigXML()))
			{
				if("browser_version".equals(arg.getKey()) && !"*".equals(arg.getValue()))
				{
					platform += " " + arg.getValue();
				}
			}
			
			for(S3ObjectSummary file : amazonService.listFiles(String.valueOf(testRun.getId()) + "/"))
			{
				if(!file.getKey().endsWith("/"))
				{
					certification.addScreenshot(amazonService.getComment(file.getKey()), platform, amazonService.getPublicLink(file));
				}
			}
		}
	
		return certification;		
	}
}