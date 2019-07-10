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

/**
 * Defines access to test registration events and callbacks
 */
public interface TestLifecycleAware {

    void onSuiteStart(SuiteAdapter adapter);

    void onSuiteFinish();

    void onTestStart(TestResultAdapter resultAdapter);

    void onTestSuccess(TestResultAdapter resultAdapter);

    void onTestFailure(TestResultAdapter adapter);

    void onTestSkipped(TestResultAdapter adapter);

    void onTestHook(TestHookable hookCallBack, TestResultAdapter adapter);

    void beforeMethodInvocation(MethodAdapter invokedMethodAdapter, TestResultAdapter adapter);

}
