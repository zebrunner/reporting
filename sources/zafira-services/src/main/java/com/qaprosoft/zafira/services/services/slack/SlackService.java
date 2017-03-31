package com.qaprosoft.zafira.services.services.slack;

import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackAttachment;
import in.ashwanthkumar.slack.webhook.SlackAttachment.Field;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qaprosoft.zafira.models.db.TestRun;
import com.qaprosoft.zafira.services.services.JenkinsService;

@Service
public class SlackService
{

	private final static String RESULTS_PATTERN = "Passed: %d, Failed: %d, Known Issues: %d, Skipped: %d";
	private final static String MAIN_PATTERN = "Test run #%1$d has been completed after %2$s\n"
			+ "%3$s\n"
			+ "<%4$s|Open in Zafira>  |  <%5$s|Open in Jenkins>";

	private final static String REV_PATTERN = "Test run #%1$d has been reviewed\n"
			+ "%2$s\n"
			+ "<%3$s|Open in Zafira>  |  <%4$s|Open in Jenkins>";

	@Value("${zafira.webservice.url}")
	private String wsURL;

	@Value("${zafira.slack.webhook_url}")
	private String slackWebHook;

	@Autowired
	private JenkinsService jenkinsService;

	public void sendAutoStatus(TestRun tr) throws IOException
	{
		String elapsed = countElapsedInSMH(tr.getElapsed());
		String zafiraUrl = wsURL + "/#!/tests/runs?id=" + tr.getId();
		String jenkinsUrl = tr.getJob().getJobURL() + "/" + tr.getBuildNumber() + "/eTAF_Report";

		String mainMsg = String.format(MAIN_PATTERN, tr.getId(), elapsed, buildRunInfo(tr), zafiraUrl, jenkinsUrl);
		String msgRes = String.format(RESULTS_PATTERN, tr.getPassed(), tr.getFailed(),
				tr.getFailedAsKnown(), tr.getSkipped());

		SlackAttachment attachment = new SlackAttachment("")
				.preText(mainMsg)
				.color(determineColor(tr))
				.addField(new Field("Test Results", msgRes, false))
				.fallback(mainMsg + "\n" + msgRes);
		new Slack(slackWebHook).push(attachment);
	}

	public void sendReviwedStatus(TestRun tr) throws IOException
	{
		String zafiraUrl = wsURL + "/#!/tests/runs?id=" + tr.getId();
		String jenkinsUrl = tr.getJob().getJobURL() + "/" + tr.getBuildNumber() + "/eTAF_Report";

		String mainMsg = String.format(REV_PATTERN, tr.getId(), buildRunInfo(tr), zafiraUrl, jenkinsUrl);
		String msgRes = String.format(RESULTS_PATTERN, tr.getPassed(), tr.getFailed(),
				tr.getFailedAsKnown(), tr.getSkipped());

		SlackAttachment attachment = new SlackAttachment("")
				.preText(mainMsg)
				.color(determineColor(tr))
				.addField(new Field("Test Results", msgRes, false))
				.fallback(mainMsg + "\n" + msgRes);
		new Slack(slackWebHook).push(attachment);
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
