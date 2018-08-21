package com.qaprosoft.zafira.services.services.jmx.models;

import in.ashwanthkumar.slack.webhook.Slack;

public class SlackType extends AbstractType
{

    private Slack slack;

    public SlackType(String webHook, String author, String picPath)
    {
        this.slack = new Slack(webHook);
        this.slack = this.slack.displayName(author);
        this.slack = this.slack.icon(picPath);
    }

    public Slack getSlack()
    {
        return slack;
    }

    public void setSlack(Slack slack)
    {
        this.slack = slack;
    }
}
