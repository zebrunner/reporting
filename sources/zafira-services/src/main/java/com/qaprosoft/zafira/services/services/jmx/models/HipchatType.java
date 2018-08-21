package com.qaprosoft.zafira.services.services.jmx.models;

import io.evanwong.oss.hipchat.v2.HipChatClient;

public class HipchatType extends AbstractType
{

    private HipChatClient hipchatClient;

    public HipchatType(String accessToken)
    {
        this.hipchatClient = new HipChatClient(accessToken);
    }

    public HipChatClient getHipchatClient()
    {
        return hipchatClient;
    }

    public void setHipchatClient(HipChatClient hipchatClient)
    {
        this.hipchatClient = hipchatClient;
    }
}
