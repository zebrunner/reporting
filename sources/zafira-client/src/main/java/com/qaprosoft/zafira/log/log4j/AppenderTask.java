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
package com.qaprosoft.zafira.log.log4j;

import com.qaprosoft.zafira.listener.ZafiraEventRegistrar;
import com.qaprosoft.zafira.log.BaseAppenderTask;
import com.qaprosoft.zafira.log.domain.MetaInfoMessage;
import com.qaprosoft.zafira.log.log4j.level.MetaInfoLevel;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class AppenderTask extends BaseAppenderTask<LoggingEvent> {

    private final Layout layout;
    private final LoggingEvent loggingEvent;

    AppenderTask(LoggingEvent loggingEvent, Layout layout) {
        this.loggingEvent = loggingEvent;
        this.layout = layout;
    }

    @Override
    protected LoggingEvent getEventObject() {
        return loggingEvent;
    }

    @Override
    protected String getTestId() {
        String result;
        boolean isMetaInfoLevel = loggingEvent.getLevel().equals(MetaInfoLevel.META_INFO);
        if (isMetaInfoLevel && ((MetaInfoMessage) loggingEvent.getMessage()).getHeaders().get("CI_TEST_ID") != null) {
            result = ((MetaInfoMessage) loggingEvent.getMessage()).getHeaders().get("CI_TEST_ID");
        } else {
            result = ZafiraEventRegistrar.getThreadCiTestId();
        }
        return result;
    }

    @Override
    protected String getJsonPayload() {
        return layout.format(loggingEvent);
    }

    @Override
    protected String getEventType() {
        return loggingEvent.getLevel().toString();
    }

}