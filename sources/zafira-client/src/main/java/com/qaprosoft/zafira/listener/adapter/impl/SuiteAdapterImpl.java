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
package com.qaprosoft.zafira.listener.adapter.impl;

import com.qaprosoft.zafira.listener.adapter.MethodAdapter;
import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.ISuite;
import org.testng.ITestNGMethod;

import java.util.List;
import java.util.stream.Collectors;

public class SuiteAdapterImpl implements SuiteAdapter {

    private static final String ERR_MSG_SUITE_REQUIRED = "TestNG suite is required to apply its data";

    private final ISuite suite;

    public SuiteAdapterImpl(ISuite suite) {
        this.suite = suite;
    }

    @Override
    public Object getSuite() {
        suiteNotNull();
        return suite;
    }

    @Override
    public String getSuiteParameter(String name) {
        suiteNotNull();
        return suite.getParameter(name);
    }

    @Override
    public String getSuiteFileName() {
        suiteNotNull();
        return suite.getXmlSuite().getFileName();
    }

    @Override
    public String getSuiteName() {
        suiteNotNull();
        return suite.getName();
    }

    @Override
    public String[] getSuiteDependsOnMethods() {
        suiteNotNull();
        String[] allDependentMethods = suite.getAllMethods().stream()
                                            .map(ITestNGMethod::getMethodsDependedUpon)
                                            .reduce(ArrayUtils.EMPTY_STRING_ARRAY, ArrayUtils::addAll);
        return allDependentMethods;
    }

    @Override
    public List<MethodAdapter> getMethodAdapters() {
        suiteNotNull();
        return suite.getAllMethods().stream()
                    .map(MethodAdapterImpl::new)
                    .collect(Collectors.toList());
    }

    private void suiteNotNull() {
        if(suite == null) {
            throw new RuntimeException(ERR_MSG_SUITE_REQUIRED);
        }
    }

}
