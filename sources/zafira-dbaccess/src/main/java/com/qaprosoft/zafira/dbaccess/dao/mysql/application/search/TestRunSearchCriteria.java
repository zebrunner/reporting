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
package com.qaprosoft.zafira.dbaccess.dao.mysql.application.search;

import com.qaprosoft.zafira.models.db.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class TestRunSearchCriteria extends SearchCriteria implements DateSearchCriteria {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date toDate;

    private Long id;
    private Long testSuiteId;
    private String environment;
    private String platform;
    private Status status;
    private Boolean reviewed;
    private FilterSearchCriteria filterSearchCriteria;

    public TestRunSearchCriteria() {
        super.setSortOrder(SortOrder.DESC);
    }

    public void setFromDateString(String fromDate) throws ParseException {
        this.fromDate = new SimpleDateFormat("MM-dd-yyyy").parse(fromDate);
    }

    public void setToDateString(String toDate) throws ParseException {
        this.toDate = new SimpleDateFormat("MM-dd-yyyy").parse(toDate);
    }

}