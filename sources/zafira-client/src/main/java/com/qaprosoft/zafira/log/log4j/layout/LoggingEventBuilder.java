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

import com.qaprosoft.zafira.log.domain.MetaInfoMessage;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.qaprosoft.zafira.log.log4j.level.MetaInfoLevel.META_INFO;

class LoggingEventBuilder {

    private final JSONObject object;

    LoggingEventBuilder(JSONObject object) {
        this.object = object;
    }

    /**
     * Converts LoggingEvent Throwable to JSON object
     * @param event - event from logger
     * @throws JSONException - unable to parse json
     */
    LoggingEventBuilder writeThrowable(LoggingEvent event) throws JSONException {
        ThrowableInformation throwableInformation = event.getThrowableInformation();
        if (throwableInformation != null) {
            Throwable t = throwableInformation.getThrowable();
            JSONObject throwable = new JSONObject();

            throwable.put("message", t.getMessage());
            throwable.put("className", t.getClass().getCanonicalName());
            List<JSONObject> traceObjects = new ArrayList<>();
            for (StackTraceElement ste : t.getStackTrace()) {
                JSONObject element = new JSONObject();
                element.put("class", ste.getClassName());
                element.put("method", ste.getMethodName());
                element.put("line", ste.getLineNumber());
                element.put("file", ste.getFileName());
                traceObjects.add(element);
            }

            object.put("stackTrace", traceObjects);
            object.put("throwable", throwable);
        }
        return this;
    }

    /**
     * Converts basic LoggingEvent properties to JSON object
     * @param event - event from logger
     * @throws JSONException - unable to parse json
     */
    LoggingEventBuilder writeBasic(LoggingEvent event) throws JSONException {
        object.put("threadName", event.getThreadName());
        object.put("level", event.getLevel().toString());
        object.put("timestamp", System.currentTimeMillis());
        if (event.getLevel().equals(META_INFO)) {
            MetaInfoMessage metaInfoMessage = (MetaInfoMessage) event.getMessage();
            object.put("message", metaInfoMessage.getMessage());
            object.put("headers", new JSONObject(metaInfoMessage.getHeaders()));
        } else {
            object.put("message", event.getMessage());
        }
        object.put("logger", event.getLoggerName());
        return this;
    }

    /**
     * Converts result json object to string
     * @return result json payload
     */
    String build() {
        return object.toString();
    }

}
