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
package com.qaprosoft.zafira.services.services.application.jmx.amazon;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.qaprosoft.zafira.models.db.Setting;
import com.qaprosoft.zafira.services.services.application.SettingsService;
import com.qaprosoft.zafira.services.services.application.jmx.CryptoService;
import com.qaprosoft.zafira.services.services.application.jmx.IJMXService;
import com.qaprosoft.zafira.services.services.application.jmx.context.CloudFrontContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;

import java.util.Date;
import java.util.List;

import static com.qaprosoft.zafira.models.db.Setting.Tool.CLOUD_FRONT;

@ManagedResource(objectName = "bean:name=cloudFrontService", description = "Cloud front init Managed Bean", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
public class CloudFrontService implements IJMXService<CloudFrontContext>, IURLGenerator {

    private static final Logger LOGGER = Logger.getLogger(CloudFrontService.class);

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CryptoService cryptoService;

    @Override
    public void init() {

        String distributionDomain = null;
        String keyPairId = null;
        byte[] privateKey = null;
        String fileName = null;

        try {
            List<Setting> amazonSettings = settingsService.getSettingsByTool(CLOUD_FRONT);
            for (Setting setting : amazonSettings) {
                if (setting.isEncrypted()) {
                    setting.setValue(cryptoService.decrypt(setting.getValue()));
                }
                switch (Setting.SettingType.valueOf(setting.getName())) {
                    case CLOUD_FRONT_DISTRIBUTION_DOMAIN:
                        distributionDomain = setting.getValue();
                        break;
                    case CLOUD_FRONT_KEY_PAIR_ID:
                        keyPairId = setting.getValue();
                        break;
                    case CLOUD_FRONT_PRIVATE_KEY:
                        privateKey = setting.getFile();
                        fileName = setting.getValue();
                        break;
                    default:
                        break;
                }
            }
            init(distributionDomain, keyPairId, privateKey, fileName);
        } catch (Exception e) {
            LOGGER.error("Setting does not exist", e);
        }
    }

    @ManagedOperation(description = "Cloud front initialization")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "distributionDomain", description = "Cloud front distribution domain"),
            @ManagedOperationParameter(name = "keyPairId", description = "Cloud front key pair id"),
            @ManagedOperationParameter(name = "privateKey", description = "Cloud front private key"),
            @ManagedOperationParameter(name = "fileName", description = "Cloud front file name")})
    public void init(String distributionDomain, String keyPairId, byte[] privateKey, String fileName) {
        try {
            if (!StringUtils.isBlank(distributionDomain) && !StringUtils.isBlank(keyPairId) && privateKey != null && !StringUtils.isBlank(fileName)) {
                putContext(CLOUD_FRONT, new CloudFrontContext(distributionDomain, keyPairId, privateKey, fileName));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to initialize Amazon integration: " + e.getMessage());
        }
    }

    @Override
    public boolean isConnected() {
        return getCloudFrontType() != null;
    }

    @Override
    public String generatePresignedURL(Integer expiresIn, String key) throws Exception {
        final Date expectedDate = DateUtils.addSeconds(new Date(), expiresIn);
        final Date expirationDate = expectedDate.after(DateUtils.addDays(new Date(), 7)) ? DateUtils.addDays(new Date(), 7) : expectedDate;
        return getContext(CLOUD_FRONT) != null ? CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                SignerUtils.Protocol.https,
                getContext(CLOUD_FRONT).getDistributionDomain(),
                getContext(CLOUD_FRONT).getPrivateKey(),
                key,
                getContext(CLOUD_FRONT).getKeyPairId(),
                expirationDate) : null;
    }

    @ManagedAttribute(description = "Get current cloud front entity")
    public CloudFrontContext getCloudFrontType() {
        return getContext(CLOUD_FRONT);
    }
}
