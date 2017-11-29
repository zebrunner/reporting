package com.qaprosoft.zafira.services.util;

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.DateSearchCriteria;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateFormatter {

    public static void actualizeSearchCriteriaDate(DateSearchCriteria sc){
        if(sc.getDate() != null){
            if (isToday(sc.getDate())){
                sc.setDate(formatDateToStartOfDay(new Date()));
            } else {
                sc.setDate(formatDateToStartOfDay(sc.getDate()));
            }
        }
    }

    public static boolean isToday(Date date){
        boolean today  = false;
        LocalDate ld = new java.sql.Date(date.getTime() ).toLocalDate();
        if (ld.isEqual(LocalDate.now()) || ld.isAfter(LocalDate.now())){
            today = true;
        }
        return today;
    }

    public static Date formatDateToStartOfDay (Date date){
        LocalDate ld = new java.sql.Date(date.getTime() ).toLocalDate();
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
