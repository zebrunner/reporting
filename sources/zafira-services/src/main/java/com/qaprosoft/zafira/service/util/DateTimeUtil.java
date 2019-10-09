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

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.DateSearchCriteria;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Provides set of utility methods simplifying DateTime related conversions and calculation
 */
public class DateTimeUtil {

    /**
     * Force set search criteria date to start of day
     * @param sc search criteria to be actualized
     */
    public static void actualizeSearchCriteriaDate(DateSearchCriteria sc) {
        Date date = sc.getDate();
        if (date != null) {
            sc.setDate(toStartOfDay(date));
        }
    }

    public static long toSecondsSinceDateToNow(Date date){
        return calculateDuration(date, Calendar.getInstance().getTime()).toSeconds();
    }

    public static Date toDateSinceNowPlusSeconds(long durationInSeconds) {
        return toDate(LocalDateTime.now().plusSeconds(durationInSeconds));
    }

    public static Duration calculateDuration(Date startDate, Date endDate) {
        return Duration.between(toLocalDateTime(startDate), toLocalDateTime(endDate));
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static Date toStartOfDay(Date date) {
        LocalDate localDate = toLocalDate(date);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}