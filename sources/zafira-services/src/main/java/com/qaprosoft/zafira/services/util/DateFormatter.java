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
package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.dbaccess.dao.mysql.application.search.DateSearchCriteria;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * DateFormatter utility class.
 * 
 * @author itsvirko
 */
public class DateFormatter {
    public static void actualizeSearchCriteriaDate(DateSearchCriteria sc) {
        if (sc.getDate() != null) {
            if (isToday(sc.getDate())) {
                sc.setDate(formatDateToStartOfDay(new Date()));
            } else {
                sc.setDate(formatDateToStartOfDay(sc.getDate()));
            }
        }
    }

    private static boolean isToday(Date date) {
        boolean today = false;
        LocalDate ld = new java.sql.Date(date.getTime()).toLocalDate();
        if (ld.isEqual(LocalDate.now())) {
            today = true;
        }
        return today;
    }

    private static Date formatDateToStartOfDay(Date date) {
        LocalDate ld = new java.sql.Date(date.getTime()).toLocalDate();
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts
     * @param dateToConvert
     * into LocalDateTime format.
     * @return LocalDateTime value.
     */

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
    }

    /**
     * Calculates number of seconds since Date
     * @param startedAtDate
     * till the current moment.
     * @return Integer value.
     */
    public static Integer calculateDurationFromDate(Date startedAtDate){
        Duration duration = calculateDuration(startedAtDate, Calendar.getInstance().getTime());
        return Long.valueOf(duration.toSeconds()).intValue();
    }

    /**
     * Calculates date from now till
     * @param durationInSeconds
     * is passed.
     * @return Date value.
     */

    public static Date calculateDurationToDate(Integer durationInSeconds){
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime finishedAtTime = localDateTime.plusSeconds(durationInSeconds.longValue());
        return Date.from(finishedAtTime.atZone( ZoneId.systemDefault()).toInstant());
    }

    /**
     * Calculates duration between
     * @param startedAtDate and
     * @param finishedAtDate .
     * @return Duration object as value.
     */
    public static Duration calculateDuration(Date startedAtDate, Date finishedAtDate){
        LocalDateTime startedAt = convertToLocalDateTime(startedAtDate);
        LocalDateTime finishedAt = convertToLocalDateTime(finishedAtDate);
        return Duration.between(startedAt, finishedAt);
    }

}