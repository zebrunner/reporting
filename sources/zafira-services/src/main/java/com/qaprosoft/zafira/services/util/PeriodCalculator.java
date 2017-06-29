package com.qaprosoft.zafira.services.util;


import com.qaprosoft.zafira.dbaccess.dao.mysql.search.SearchCriteria;
import com.qaprosoft.zafira.dbaccess.dao.mysql.search.TestRunSearchCriteria;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;


public class PeriodCalculator {

     public static SearchCriteria setPeriod(TestRunSearchCriteria sc){
         String periodName = sc.getPeriod();
         Year currentYear = Year.now();
         LocalDate currentDate = LocalDate.now();
         LocalDate firstDayOfTheWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);
         switch (periodName) {
             case "today":
                 sc.setDate(convertDate(currentDate));
                 break;
             case "yesterday":
                 sc.setDate(convertDate(currentDate.minusDays(1)));
                 break;
             case "this_week":
                 sc.setToDate(convertDate(currentDate));
                 sc.setFromDate(convertDate(firstDayOfTheWeek));
                 break;
             case "last_week":
                 sc.setToDate(convertDate(firstDayOfTheWeek.minusDays(1)));
                 sc.setFromDate(convertDate(firstDayOfTheWeek.minusWeeks(1)));
                 break;
             case "this_month":
                 sc.setToDate(convertDate(currentDate));
                 sc.setFromDate(convertDate(getStartDate(currentYear, currentDate.getMonth())));
                 break;
             case "last_month":
                 sc.setToDate(convertDate(getEndDate(currentYear, currentDate.getMonth().minus(1))));
                 sc.setFromDate(convertDate(getStartDate(currentYear, currentDate.getMonth().minus(1))));
                 break;
             case "this_qtr":
                 sc.setToDate(convertDate(currentDate));
                 sc.setFromDate(convertDate(getStartDate(currentYear, currentDate.getMonth().firstMonthOfQuarter())));
                 break;
             case "last_qtr":
                 sc.setToDate(convertDate(getEndDate(currentYear, currentDate.getMonth().firstMonthOfQuarter().minus(1))));
                 sc.setFromDate(convertDate(getStartDate(currentYear, currentDate.getMonth().firstMonthOfQuarter().minus(3))));
                 break;
             case "this_year":
                 sc.setToDate(convertDate(currentDate));
                 sc.setFromDate(convertDate(currentYear.atDay(1)));
                 break;
             case "last_year":
                 int lastYearLength = currentYear.minusYears(1).length();
                 sc.setToDate(convertDate(currentYear.minusYears(1).atDay(lastYearLength)));
                 sc.setFromDate(convertDate(currentYear.minusYears(1).atDay(1)));
                 break;
         }
         return sc;
     }

    private static LocalDate getStartDate(Year year, Month month){
        return year.atMonth(month).atDay(1);
    }

    private static LocalDate getEndDate(Year year, Month month){
        return year.atMonth(month).atDay(month.length(year.isLeap()));
    }

    private static Date convertDate(LocalDate localDate){
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}