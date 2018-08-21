/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
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
package com.qaprosoft.zafira.services.services.jmx;

import static com.qaprosoft.zafira.models.db.Setting.Tool.HIPCHAT;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.SettingsService;
import com.qaprosoft.zafira.services.services.jmx.models.HipchatType;

import io.evanwong.oss.hipchat.v2.commons.NoContent;
import io.evanwong.oss.hipchat.v2.oauth.Session;
import io.evanwong.oss.hipchat.v2.rooms.MessageFormat;
import io.evanwong.oss.hipchat.v2.users.UserItem;

@ManagedResource(objectName = "bean:name=hipchatService", description = "Hipchat init Managed Bean", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
public class HipchatService implements IJMXService<HipchatType> {
    private static final Logger LOGGER = Logger.getLogger(HipchatService.class);

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CryptoService cryptoService;

    @Override
    @PostConstruct
    public void init() {
        String accessToken = null;

        try {
            List<Setting> hipchatSettings = settingsService.getSettingsByTool(HIPCHAT);
            for (Setting setting : hipchatSettings) {
                if (setting.isEncrypted()) {
                    setting.setValue(cryptoService.decrypt(setting.getValue()));
                }
                switch (Setting.SettingType.valueOf(setting.getName())) {
                case HIPCHAT_ACCESS_TOKEN:
                    accessToken = setting.getValue();
                    break;
                default:
                    break;
                }
            }
            init(accessToken);
        } catch (Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    @ManagedOperation(description = "Hipchat initialization")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "accessToken", description = "Hipchat access token") })
    public void init(String accessToken) {
        try {
            if (!StringUtils.isEmpty(accessToken)) {
                putType(HIPCHAT, new HipchatType(accessToken));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize Hipchat integration: " + e.getMessage());
        }
    }

    public Session getSession() {
        Session session = null;
        try {
            session = getHipchatType().getHipchatClient().prepareGetSessionRequestBuilder().build().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e);
        }
        return session;
    }

    public UserItem getUserByIdOrEmail(String idOrEmail) {
        UserItem userItem = null;
        try {
            userItem = getHipchatType().getHipchatClient().prepareViewUserRequestBuilder(idOrEmail).build().execute()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e);
        }
        return userItem;
    }

    public boolean sendPrivateMessageToUser(String userIdOrEmail, String text, MessageFormat messageFormat) {
        NoContent noContent = null;
        try {
            noContent = getHipchatType().getHipchatClient().preparePrivateMessageUserRequestBuilder(userIdOrEmail, text)
                    .setMessageFormat(messageFormat).build().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e);
        }
        return noContent != null;
    }

    public boolean sendPrivateMessageToRoom(String userIdOrName, String text) {
        NoContent noContent = null;
        try {
            noContent = getHipchatType().getHipchatClient().prepareSendRoomMessageRequestBuilder(userIdOrName, text)
                    .build().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e);
        }
        return noContent != null;
    }

    public boolean sendNotificationToRoom(String userIdOrName, String text, MessageFormat messageFormat) {
        NoContent noContent = null;
        try {
            noContent = getHipchatType().getHipchatClient()
                    .prepareSendRoomNotificationRequestBuilder(userIdOrName, text)
                    .setMessageFormat(messageFormat)
                    .build().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e);
        }
        return noContent != null;
    }

    @Override
    public boolean isConnected() {
        try {
            return getHipchatType().getHipchatClient().prepareGetSessionRequestBuilder().build().execute()
                    .get() != null;
        } catch (Exception e) {
            return false;
        }
    }

    @ManagedAttribute(description = "Get current hipchat entity")
    public HipchatType getHipchatType() {
        return getType(HIPCHAT);
    }
}
