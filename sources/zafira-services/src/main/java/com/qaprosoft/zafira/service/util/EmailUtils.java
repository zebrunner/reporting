/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
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
 *******************************************************************************/
package com.qaprosoft.zafira.service.util;

import com.qaprosoft.zafira.models.db.Attachment;
import com.qaprosoft.zafira.service.exception.IllegalOperationException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.ContentDisposition;

import java.io.IOException;

import static com.qaprosoft.zafira.service.exception.IllegalOperationException.IllegalOperationErrorDetail.ATTACHMENT_RESOURCE_NOT_NULL;

public class EmailUtils {

    private static final String ERR_MSG_ATTACHMENT_RESOURCE_IS_NULL = "Email attachment resource can not be null";

    private static final String MAIL_MESSAGE_CID_PATTERN = "<%s>";
    private static final String INLINE_ATTACHMENT_HEADER = "inline";

    public static String[] obtainRecipients(String recipientsLine) {
        if (StringUtils.isEmpty(recipientsLine)) {
            return new String[] {};
        } else {
            return recipientsLine.trim()
                                 .replaceAll(",", " ")
                                 .replaceAll(";", " ")
                                 .replaceAll("\\[]", " ")
                                 .split(" ");
        }
    }

    /**
     * Uses to add an inline image with filename to mail message
     * Native Spring mail implementation does not provide a functionality to use disposition header name with filename part with inline source
     * @param msg - prepared message
     * @param attachment - an attachment to add
     * @throws MessagingException on some exception during message ransformation and initialization
     */
    public static void addNamedInline(MimeMessageHelper msg, Attachment attachment) throws MessagingException, IOException {
        if (attachment.getFile() == null || StringUtils.isEmpty(attachment.getFilename())) {
            throw new IllegalOperationException(ATTACHMENT_RESOURCE_NOT_NULL, ERR_MSG_ATTACHMENT_RESOURCE_IS_NULL);
        }
        String cid = attachment.getFilename().replaceAll("[^A-Za-z0-9]", "_");
        msg.addInline(cid, attachment.getFile());

        BodyPart bodyPart = msg.getMimeMultipart().getBodyPart(String.format(MAIL_MESSAGE_CID_PATTERN, cid));

        // Removes any old dispositions ('Content-Disposition' headers)
        bodyPart.setDisposition(null);

        // Creates a new disposition with file name and inserts created disposition into message header
        ContentDisposition contentDisposition = new ContentDisposition(INLINE_ATTACHMENT_HEADER);
        contentDisposition.setParameter("filename", attachment.getFilename());
        bodyPart.setDisposition(contentDisposition.toString());
    }
}
