package com.qaprosoft.zafira.batchservices.tasks;

import com.qaprosoft.zafira.services.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

public class SendEmailNotificationToUser {

    @Autowired
    private EmailService emailService;

    public void runTask()
    {

    }

}
