package com.qaprosoft.zafira.ws.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.models.db.config.Argument;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.AmazonService;
import com.qaprosoft.zafira.services.services.TestConfigService;
import com.qaprosoft.zafira.services.services.TestRunService;
import com.qaprosoft.zafira.models.dto.CertificationType;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("certification")
public class CertificationController extends AbstractController
{
	@Autowired
	private TestRunService testRunService;
	
	@Autowired
	private AmazonService amazonService;
	
	@Autowired
	private TestConfigService testConfigService;
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView index() throws IOException, InterruptedException, ServiceException
	{
		return new ModelAndView("certification/index");
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(path="details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody CertificationType getCertifcationDetails(@RequestParam(value="upstreamJobId", required=true) Long upstreamJobId, @RequestParam(value="upstreamJobBuildNumber", required=true) Integer upstreamJobBuildNumber) throws ServiceException
	{
		CertificationType certification = new CertificationType();
		
		for(TestRun testRun : testRunService.getTestRunsByUpstreamJobIdAndUpstreamJobBuildNumber(upstreamJobId, upstreamJobBuildNumber))
		{
			String platform = testRun.getPlatform();
			for(Argument arg : testConfigService.readConfigArgs(testRun.getConfigXML(), false))
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
