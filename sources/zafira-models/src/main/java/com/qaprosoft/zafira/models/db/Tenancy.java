/*******************************************************************************
 * Copyright 2013-2018 QaProSoft (http://www.qaprosoft.com).
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.qaprosoft.zafira.models.db;

public class Tenancy extends AbstractEntity {

    private static final long serialVersionUID = -4999394495425059506L;

    private static final String DEFAULT_TENANT = "zafira";
    private static final String MANAGEMENT_SCHEMA = "management";
    private static final String[] DEFAULT_NAMES = {DEFAULT_TENANT, MANAGEMENT_SCHEMA};

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getDefaultTenant() {
        return DEFAULT_TENANT;
    }

    public static String getManagementSchema() {
        return MANAGEMENT_SCHEMA;
    }

    public static String[] getDefaultNames() {
        return DEFAULT_NAMES;
    }
}
