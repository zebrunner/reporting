package com.qaprosoft.zafira.services.services.emails;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.models.db.Monitor;

import java.util.List;

/**
 * @author Kirill Bugrim
 *
 *  @version 1.0
 */

public class MonitorEmailMessageNotification implements IEmailMessage {

    private static final String TEMPLATE = "monitorEmails.ftl";

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    private Monitor monitor;

    public MonitorEmailMessageNotification(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public String getSubject() {
        return "Subject";
    }

    @Override
    public String getText() {
        return "text";
    }

    @Override
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public List<Attachment> getAttachments() {
        return null;
    }
}
