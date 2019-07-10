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
package com.qaprosoft.zafira.listener;

import com.qaprosoft.zafira.listener.adapter.MethodAdapter;
import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.listener.adapter.impl.MethodAdapterImpl;
import com.qaprosoft.zafira.listener.adapter.impl.SuiteAdapterImpl;
import com.qaprosoft.zafira.listener.adapter.impl.TestResultAdapterImpl;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that provides integration with Zafira reporting web-service.
 * Accumulates test results and handles rerun failures logic.
 * 
 * @author akhursevich
 */
public class ZafiraListener implements ISuiteListener, ITestListener, IHookable, IInvokedMethodListener {

    private final TestLifecycleAware listener;

    public ZafiraListener() {
        this.listener = new ZafiraEventRegistrar();
    }

    @Override
    public void onStart(ISuite suiteContext) {
        SuiteAdapter adapter = new SuiteAdapterImpl(suiteContext);
        listener.onSuiteStart(adapter);
    }

    @Override
    public void onTestStart(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestStart(adapter);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestSuccess(adapter);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestFailure(adapter);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestFailure(adapter);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestSkipped(adapter);
    }

    @Override
    public void onFinish(ISuite suiteContext) {
        listener.onSuiteFinish();
    }

    @Override
    public void onStart(ITestContext context) {
        // Do nothing
    }

    @Override
    public void onFinish(ITestContext context) {
        // Do nothing
    }

    @Override
    public void run(IHookCallBack hookCallBack, ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestHook(adapterToRun -> hookCallBack.runTestMethod(result), adapter);
    }

    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        MethodAdapter methodAdapter = new MethodAdapterImpl(invokedMethod.getTestMethod());
        listener.beforeMethodInvocation(methodAdapter, adapter);
    }

    @Override
    public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        // Do nothing
    }

}
