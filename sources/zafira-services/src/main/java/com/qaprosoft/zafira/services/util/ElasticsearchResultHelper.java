/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.qaprosoft.zafira.services.util;

import org.elasticsearch.search.SearchHit;

import java.util.HashMap;
import java.util.Map;

public class ElasticsearchResultHelper {

    private static final String HEADERS_FIELD_NAME = "headers";
    private static final String AMAZON_PATH_FIELD_NAME = "AMAZON_PATH";
    private static final String MESSAGE_FIELD_NAME = "message";

    public static HashMap getHeaders(SearchHit hit) {
        return (HashMap) getSourceMap(hit).get(HEADERS_FIELD_NAME);
    }

    public static String getAmazonPath(SearchHit hit) {
        String result = null;
        HashMap headers = getHeaders(hit);
        if(headers != null) {
            Object amazonPath = headers.get(AMAZON_PATH_FIELD_NAME);
            result = amazonPath != null ? amazonPath.toString() : null;
        }
        return result;
    }

    public static String getMessage(SearchHit hit) {
        Object message = getSourceMap(hit).get(MESSAGE_FIELD_NAME);
        return message != null ? message.toString() : null;
    }

    private static Map<String, Object> getSourceMap(SearchHit hit) {
        return hit.getSourceAsMap();
    }
}
