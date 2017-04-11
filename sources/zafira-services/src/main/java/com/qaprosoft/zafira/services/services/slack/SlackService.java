package com.qaprosoft.zafira.services.services.slack;

import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackAttachment.Field;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.exceptions.ServiceException;
import com.qaprosoft.zafira.services.services.JenkinsService;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.SettingsService.SettingType;

@Service
public class SlackService
{

	private final static String RESULTS_PATTERN = "Passed: %d, Failed: %d, Known Issues: %d, Skipped: %d";
	private final static String MAIN_PATTERN = "Test run #%1$d has been completed after %2$s with status %3$s\n"
			+ "%4$s\n"
			+ "<%5$s|Open in Zafira>  |  <%6$s|Open in Jenkins>";

	private final static String REV_PATTERN = "Test run #%1$d has been reviewed. Status: %2$s\n"
			+ "%3$s\n"
			+ "<%4$s|Open in Zafira>  |  <%5$s|Open in Jenkins>";

	@Value("${zafira.webservice.url}")
	private String wsURL;

	@Value("${zafira.slack.author}")
	private String slackAuthor;

	@Value("${zafira.slack.pic_path}")
	private String slackPicPath;

	@Autowired
	private JenkinsService jenkinsService;

	@Autowired
	private SettingsService settingsService;

	public void sendAutoStatus(TestRun tr) throws IOException, ServiceException
	{
		Slack s = prepareSlackInst(tr);
		if (s != null)
		{
			String elapsed = countElapsedInSMH(tr.getElapsed());
			String zafiraUrl = wsURL + "/#!/tests/runs?id=" + tr.getId();
			String jenkinsUrl = tr.getJob().getJobURL() + "/" + tr.getBuildNumber();
			String status = tr.getStatus().toString();

			String mainMsg = String.format(MAIN_PATTERN, tr.getId(), elapsed, status, buildRunInfo(tr), zafiraUrl, jenkinsUrl);
			String msgRes = String.format(RESULTS_PATTERN, tr.getPassed(), tr.getFailed(),
					tr.getFailedAsKnown(), tr.getSkipped());

			SlackAttachment attachment = new SlackAttachment("")
					.preText(mainMsg)
					.color(determineColor(tr))
					.addField(new Field("Test Results", msgRes, false))
					.fallback(mainMsg + "\n" + msgRes);
			s.push(attachment);
		}
	}

	/**
	 * 
	 * @param tr
	 * @return 'true' if notification about review was successfully sent
	 * @throws IOException
	 * @throws ServiceException
	 */
	public boolean sendReviwedStatus(TestRun tr) throws IOException, ServiceException
	{
		Slack s = prepareSlackInst(tr);
		if (s != null)
		{
			String zafiraUrl = wsURL + "/#!/tests/runs?id=" + tr.getId();
			String jenkinsUrl = tr.getJob().getJobURL() + "/" + tr.getBuildNumber();
			String status = tr.getStatus().toString();

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
			s.push(attachment);
			return true;
		}
		return false;
	}

	private Slack prepareSlackInst(TestRun tr) throws ServiceException
	{
		Slack s = null;
		String wH = getWebhook();
		if (wH != null)
		{
			String channel = getChannelMapping(tr);
			if (channel != null)
			{
				s = new Slack(wH);
				s = s.sendToChannel(channel);
				s = s.displayName(slackAuthor);
				s = s.icon(slackPicPath);
			}
		}
		return s;
	}

	public String getWebhook() throws ServiceException
	{
		String wH = null;
		if (settingsService.getSettingByName(SettingType.SLACK_WEB_HOOK_URL) != null)
		{
			wH = settingsService.getSettingByName(SettingType.SLACK_WEB_HOOK_URL).getValue();
		}
		if (wH != null && !StringUtils.isEmpty(wH))
		{
			return wH;
		}
		return wH;
	}

	public String getChannelMapping(TestRun tr) throws ServiceException
	{
		List<Setting> sList = settingsService.getAllSettings();
		String pattern = StringUtils.substringBeforeLast(SettingType.SLACK_NOTIF_CHANNEL_EXAMPLE.toString(), "_");
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
		Map<String, String> jenkinsParams = jenkinsService.getBuildParameters(tr.getJob(), tr.getBuildNumber());
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

}
