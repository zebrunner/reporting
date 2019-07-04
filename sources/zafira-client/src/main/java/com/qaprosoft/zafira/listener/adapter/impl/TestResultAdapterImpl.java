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
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultStatus;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.SuiteRunner;
import org.testng.TestRunner;
import org.testng.internal.Configuration;
import org.testng.internal.TestResult;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qaprosoft.zafira.listener.adapter.TestResultStatus.UNKNOWN;

public class TestResultAdapterImpl implements TestResultAdapter {

    private static final String ERR_MSG_TEST_RESULT_REQUIRED = "TestNG test result is required to apply its data";

    private final ITestResult testResult;

    public TestResultAdapterImpl(ITestResult testResult) {
        this.testResult = testResult;
    }

    public TestResultAdapterImpl(MethodAdapter adapter) {
        ITestResult instance = null;
        if (adapter != null) {
            ITestNGMethod method = (ITestNGMethod) adapter.getMethod();
            SuiteRunner suiteRunner = new SuiteRunner(new Configuration(), new XmlSuite(), "");
            TestRunner testRunner = new TestRunner(new Configuration(), suiteRunner,
                    method.getXmlTest(), false, null, new ArrayList<>());
            instance = new TestResult(method.getTestClass(), method.getInstance(), method, null, 0, 0, testRunner);
        }
        this.testResult = instance;
    }

    @Override
    public ITestResult getTestResult() {
        testResultNotNull();
        return testResult;
    }

    @Override
    public String getName() {
        testResultNotNull();
        return testResult.getName();
    }

    @Override
    public Object[] getParameters() {
        testResultNotNull();
        return testResult.getParameters();
    }

    @Override
    public void setAttribute(String name, Object value) {
        testResultNotNull();
        testResult.setAttribute(name, value);
    }

    @Override
    public Throwable getThrowable() {
        testResultNotNull();
        return testResult.getThrowable();
    }

    @Override
    public TestResultStatus getStatus() {
        testResultNotNull();
        return Arrays.stream(TestResultStatus.values())
                     .filter(testResultStatus -> testResultStatus.getCode() == testResult.getStatus())
                     .findFirst()
                     .orElse(UNKNOWN);
    }

    @Override
    public Set<TestResultAdapter> getFailedTestResults() {
        testResultNotNull();
        return testResult.getTestContext().getFailedTests().getAllResults().stream()
                         .map(TestResultAdapterImpl::new)
                         .collect(Collectors.toSet());
    }

    @Override
    public Set<TestResultAdapter> getSkippedTestResults() {
        testResultNotNull();
        return testResult.getTestContext().getSkippedTests().getAllResults().stream()
                         .map(TestResultAdapterImpl::new)
                         .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getKnownClassNames() {
        testResultNotNull();
        return testResult.getTestContext().getCurrentXmlTest().getClasses().stream()
                         .map(XmlClass::getName)
                         .collect(Collectors.toSet());
    }

    @Override
    public RuntimeException getSkipExceptionInstance(String message) {
        return new SkipException(message);
    }

    @Override
    public MethodAdapter getMethodAdapter() {
        testResultNotNull();
        return new MethodAdapterImpl(testResult.getMethod());
    }

    @Override
    public SuiteAdapter getSuiteAdapter() {
        testResultNotNull();
        return new SuiteAdapterImpl(testResult.getTestContext().getSuite());
    }

    private void testResultNotNull() {
        if(testResult == null) {
            throw new RuntimeException(ERR_MSG_TEST_RESULT_REQUIRED);
        }
    }

}
