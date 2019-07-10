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
package com.qaprosoft.zafira.log.log4j.layout;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @author akhursevich
 */
public class JsonLayout extends Layout {

    /**
     * format a given LoggingEvent to a string, in this case JSONField string
     * 
     * @param loggingEvent - event from logger
     * @return String representation of LoggingEvent
     */
    @Override
    public String format(LoggingEvent loggingEvent) {
        String result = null;
        JSONObject object = new JSONObject();
        try {
            result = new LoggingEventBuilder(object).writeThrowable(loggingEvent)
                                                    .writeBasic(loggingEvent)
                                                    .build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Declares that this layout does not ignore throwable if available
     * 
     * @return ignores flag
     */
    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    /**
     * Just fulfilling the interface/abstract class requirements
     */
    @Override
    public void activateOptions() {
    }

}
