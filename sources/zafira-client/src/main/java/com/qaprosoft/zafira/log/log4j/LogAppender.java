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

import com.qaprosoft.zafira.log.BaseAppenderTask;
import com.qaprosoft.zafira.log.LogAppenderService;
import com.qaprosoft.zafira.log.impl.LogAppenderServiceImpl;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LogAppender extends AppenderSkeleton {

    private final LogAppenderService logAppenderService;

    private int history = 1000;

    public LogAppender() {
        this.logAppenderService = new LogAppenderServiceImpl();
    }

    /**
     * Submits LoggingEvent for publishing if it reaches severity threshold.
     *
     * @param loggingEvent - log event
     */
    @Override
    protected void append(LoggingEvent loggingEvent) {
        if (isAsSevereAsThreshold(loggingEvent.getLevel())) {
            BaseAppenderTask<LoggingEvent> task = new AppenderTask(loggingEvent, layout);
            logAppenderService.append(task);
        }
    }

    /**
     * Creates the connection, channel to RabbitMQ. Declares exchange and queue
     *
     * @see AppenderSkeleton
     */
    @Override
    public void activateOptions() {
        super.activateOptions();
        try {
            logAppenderService.connectZafira();
        }  catch (IOException | TimeoutException e) {
            errorHandler.error(e.getMessage());
        } catch (Exception e) {
            // TODO: add logging
        }
    }

    /**
     * Closes the channel and connection to RabbitMQ when shutting down the appender
     */
    @Override
    public void close() {
        try {
            logAppenderService.onClose();
        } catch (IOException | TimeoutException e) {
            errorHandler.error(e.getMessage(), e, ErrorCode.CLOSE_FAILURE);
        }
    }

    /**
     * Ensures that a Layout property is required
     *
     * @return requires layout flag
     */
    @Override
    public boolean requiresLayout() {
        return true;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

}
