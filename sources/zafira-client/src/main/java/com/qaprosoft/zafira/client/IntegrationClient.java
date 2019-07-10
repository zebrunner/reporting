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
package com.qaprosoft.zafira.client;

import com.google.api.services.sheets.v4.Sheets;

import java.io.File;
import java.util.Optional;

public interface IntegrationClient {

    /**
     * Uploads file to Amazon S3 used integration data from server
     * @param file - any file to upload
     * @param expiresIn - in seconds to generate presigned URL
     * @param keyPrefix - bucket folder name where file will be stored
     * @return url of the file in string format
     * @throws Exception throws when there are any issues with a Amazon S3 connection
     */
    String uploadFile(File file, Integer expiresIn, String keyPrefix) throws Exception;

    Optional<Sheets> getSpreadsheetService();

}
