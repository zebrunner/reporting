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

import com.qaprosoft.zafira.dbaccess.dao.mysql.search.DateSearchCriteria;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * DateFormatter utility class.
 * 
 * @author itsvirko
 */
public class DateFormatter
{
	public static void actualizeSearchCriteriaDate(DateSearchCriteria sc)
	{
		if (sc.getDate() != null)
		{
			if (isToday(sc.getDate()))
			{
				sc.setDate(formatDateToStartOfDay(new Date()));
			}
			else
			{
				sc.setDate(formatDateToStartOfDay(sc.getDate()));
			}
		}
	}

	private static boolean isToday(Date date)
	{
		boolean today = false;
		LocalDate ld = new java.sql.Date(date.getTime()).toLocalDate();
		if (ld.isEqual(LocalDate.now()))
		{
			today = true;
		}
		return today;
	}

	private static Date formatDateToStartOfDay(Date date)
	{
		LocalDate ld = new java.sql.Date(date.getTime()).toLocalDate();
		return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
}