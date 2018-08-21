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
package com.qaprosoft.zafira.services.services.jmx;

import static com.qaprosoft.zafira.models.db.Setting.SettingType.SLACK_NOTIF_CHANNEL_EXAMPLE;
import static com.qaprosoft.zafira.models.db.Setting.SettingType.SLACK_WEB_HOOK_URL;

import com.qaprosoft.zafira.services.services.jmx.models.SlackType;
import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackAttachment.Field;
import in.ashwanthkumar.slack.webhook.SlackMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.emails.TestRunResultsEmail;

@ManagedResource(objectName="bean:name=slackService", description="Slack init Managed Bean",
		currencyTimeLimit=15, persistPolicy="OnUpdate", persistPeriod=200,
		persistLocation="foo", persistName="bar")
public class SlackService implements IJMXService<SlackType>
{

	private final static String RESULTS_PATTERN = "Passed: %d, Failed: %d, Known Issues: %d, Skipped: %d";
	private final static String MAIN_PATTERN = "Test run #%1$d has been completed after %2$s with status %3$s\n"
			+ "%4$s\n"
			+ "<%5$s|Open in Zafira>  |  <%6$s|Open in Jenkins>";

	private final static String REV_PATTERN = "Test run #%1$d has been reviewed. Status: %2$s\n"
			+ "%3$s\n"
			+ "<%4$s|Open in Zafira>  |  <%5$s|Open in Jenkins>";

	private static final Logger LOGGER = Logger.getLogger(SlackService.class);

	@Value("${zafira.webservice.url}")
	private String wsURL;
	
	@Value("${zafira.slack.image}")
	private String image;
	
	@Value("${zafira.slack.author}")
	private String author;

	@Autowired
	private JenkinsService jenkinsService;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private CryptoService cryptoService;

	@Override
	@PostConstruct
	public void init() {
		try 
		{
			init(author, image);
		} catch(Exception e) {
			LOGGER.error("Setting does not exist", e);
		}
	}

	@Override
	public boolean isConnected() {
		try {
			if(getSlack() != null) {
				getSlack().push(new SlackMessage(StringUtils.EMPTY));
			} else {
				return false;
			}
		} catch (IOException e) {
			if(((HttpResponseException) e).getStatusCode() == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
				return false;
			}
		}
		return true;
	}

	@ManagedOperation(description="Change Slack initialization")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "author", description = "Slack author"),
			@ManagedOperationParameter(name = "picPath", description = "Slack pi path")})
	public void init(String author, String picPath) throws ServiceException {
		String wH = getWebhook();
		if (wH != null)
		{
			try {
				putType(Setting.Tool.SLACK, new SlackType(wH, author, picPath));
			} catch(IllegalArgumentException e) {
				LOGGER.info("Webhook url is not provided");
			}
		}
	}

	public void sendAutoStatus(TestRun tr) throws IOException, ServiceException
	{
		String channel = getChannelMapping(tr);
		if (channel != null)
		{
			getType(Setting.Tool.SLACK).setSlack(getSlack().sendToChannel(channel));

			String elapsed = countElapsedInSMH(tr.getElapsed());
			String zafiraUrl = getWsURL() + "/#!/tests/runs/" + tr.getId();
			String jenkinsUrl = tr.getJob().getJobURL() + "/" + tr.getBuildNumber();
			String status = TestRunResultsEmail.buildStatusText(tr);

			String mainMsg = String.format(MAIN_PATTERN, tr.getId(), elapsed, status, buildRunInfo(tr), zafiraUrl, jenkinsUrl);
			String msgRes = String.format(RESULTS_PATTERN, tr.getPassed(), tr.getFailed(),
					tr.getFailedAsKnown(), tr.getSkipped());

			SlackAttachment attachment = new SlackAttachment("")
					.preText(mainMsg)
					.color(determineColor(tr))
					.addField(new Field("Test Results", msgRes, false))
					.fallback(mainMsg + "\n" + msgRes);
			getSlack().push(attachment);
		}
	}

	/**
	 * Sends reviewed status
	 * 
	 * @param tr - test run
	 * @return 'true' if notification about review was successfully sent
	 * @throws IOException - read exception
	 * @throws ServiceException - common exception
	 */
	public boolean sendReviwedStatus(TestRun tr) throws IOException, ServiceException
	{
		String channel = getChannelMapping(tr);
		if (channel != null)
		{
			getType(Setting.Tool.SLACK).setSlack(getSlack().sendToChannel(channel));

			String zafiraUrl = getWsURL() + "/#!/tests/runs/" + tr.getId();
			String jenkinsUrl = tr.getJob().getJobURL() + "/" + tr.getBuildNumber();
			String status = TestRunResultsEmail.buildStatusText(tr);

			String mainMsg = String.format(REV_PATTERN, tr.getId(), status, buildRunInfo(tr), zafiraUrl, jenkinsUrl);
			String msgRes = String.format(RESULTS_PATTERN, tr.getPassed(), tr.getFailed(),
					tr.getFailedAsKnown(), tr.getSkipped());

			SlackAttachment attachment = new SlackAttachment("")
					.preText(mainMsg)
					.color(determineColor(tr))
					.addField(new Field("Test Results", msgRes, false))
					.fallback(mainMsg + "\n" + msgRes);
			if (tr.getComments() != null)
			{
				attachment.addField(new Field("Comments", tr.getComments(), false));
			}
			getSlack().push(attachment);
			return true;
		}
		return false;
	}

	public String getWebhook() throws ServiceException {
		String wH = null;
		Setting slackWebHookURL = settingsService.getSettingByType(SLACK_WEB_HOOK_URL);
		if (slackWebHookURL != null)
		{
			if(slackWebHookURL.isEncrypted())
			{
				try {
					slackWebHookURL.setValue(cryptoService.decrypt(slackWebHookURL.getValue()));
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
			wH = slackWebHookURL.getValue();
		}
		if (wH != null && !StringUtils.isEmpty(wH))
		{
			return wH;
		}
		return wH;
	}

	public String getChannelMapping(TestRun tr) throws ServiceException
	{
		List<Setting> sList = settingsService.getSettingsByTool(Setting.Tool.SLACK);
		String pattern = StringUtils.substringBeforeLast(SLACK_NOTIF_CHANNEL_EXAMPLE.toString(), "_");
		for (Setting s : sList)
		{
			if (s.getName().startsWith(pattern))
			{
				String v = s.getValue();
				String jobs[] = v.split(";");
				if (ArrayUtils.contains(jobs, tr.getJob().getName()))
				{
					return StringUtils.substringAfter(s.getName(), pattern + "_");
				}
			}
		}
		return null;
	}

	private String buildRunInfo(TestRun tr)
	{
		StringBuilder sbInfo = new StringBuilder();
		sbInfo.append(tr.getProject().getName());
		Map<String, String> jenkinsParams = jenkinsService.getBuildParametersMap(tr.getJob(), tr.getBuildNumber());
		if (jenkinsParams != null && jenkinsParams.get("groups") != null)
		{
			sbInfo.append("(");
			sbInfo.append(jenkinsParams.get("groups"));
			sbInfo.append(")");
		}
		sbInfo.append(" | ");
		sbInfo.append(tr.getTestSuite().getName());
		sbInfo.append(" | ");
		sbInfo.append(tr.getEnv());
		sbInfo.append(" | ");
		sbInfo.append(tr.getPlatform() == null ? "no_platform" : tr.getPlatform());
		if (tr.getAppVersion() != null)
		{
			sbInfo.append(" | ");
			sbInfo.append(tr.getAppVersion());
		}
		return sbInfo.toString();
	}

	private String countElapsedInSMH(Integer elapsed)
	{
		if (elapsed != null)
		{
			int s = elapsed % 60;
			int m = (elapsed / 60) % 60;
			int h = (elapsed / (60 * 60)) % 24;
			StringBuilder sb = new StringBuilder(String.format("%02d sec", s));
			if (m > 0)
				sb.insert(0, String.format("%02d min ", m));
			if (h > 0)
				sb.insert(0, String.format("%02d h ", h));
			return sb.toString();
		}
		return null;
	}

	private String determineColor(TestRun tr)
	{
		if (tr.getPassed() > 0 && tr.getFailed() == 0 && tr.getSkipped() == 0)
		{
			return "good";
		}
		if (tr.getPassed() == 0 && tr.getFailed() == 0 && tr.getFailedAsKnown() == 0
				&& tr.getSkipped() == 0)
		{
			return "danger";
		}
		return "warning";
	}

	private String getWsURL()
	{
		return StringUtils.removeEnd(wsURL, "-ws");
	}

	@ManagedAttribute(description="Get Slack current instance")
	public Slack getSlack() {
		return getType(Setting.Tool.SLACK) != null ? getType(Setting.Tool.SLACK).getSlack() : null;
	}
}
