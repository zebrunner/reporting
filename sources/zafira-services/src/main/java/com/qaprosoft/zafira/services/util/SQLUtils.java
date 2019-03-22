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

import com.qaprosoft.zafira.services.services.application.WidgetService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SQLUtils {

    private static final Logger LOGGER = Logger.getLogger(SQLUtils.class);

    @Autowired
    private WidgetService widgetService;

    /**
     * Returns result map if query is valid or single result with key == null on sql is invalid
     * @param sql - sql query
     * @return result map
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getResult(String sql) {
        List<Map<String, Object>> result = null;
        if(sql != null) {
            try {
                result = widgetService.executeSQL(sql);
            } catch(Exception e) {
                result = new ArrayList<>();
                result.add(new HashMap<String, Object>() {
                    {
                        put(null, sql);
                    }
                });
                LOGGER.debug("String starts with 'select' but is not" + " sql or is not valid: '" + sql + "'");
            }
        }
        return result;
    }

    /**
     * Returns result list for single row result if query is valid or one item list on sql is invalid
     * @param sql - sql query
     * @return - result list
     */
    public List<Object> getSingleRowResult(String sql) {
        List<Map<String, Object>> multiRowResult = getResult(sql);
        List<Object> result = new ArrayList<>();
        if(multiRowResult != null && multiRowResult.size() > 0 && multiRowResult.get(0).keySet().size() == 1) {
            multiRowResult.forEach(resultItem -> result.add(resultItem.get(resultItem.keySet().iterator().next())));
        }
        return result;
    }
}
