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
import com.qaprosoft.zafira.listener.adapter.TestAnnotationAdapter;
import org.testng.ITestNGMethod;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;

public class MethodAdapterImpl implements MethodAdapter {

    private static final String ERR_MSG_METHOD_REQUIRED = "TestNG method is required to apply its data";

    private final ITestNGMethod method;

    public MethodAdapterImpl(ITestNGMethod method) {
        this.method = method;
    }

    @Override
    public Object getMethod() {
        methodNotNull();
        return method;
    }

    @Override
    public Annotation[] getMethodAnnotations() {
        methodNotNull();
        return method.getConstructorOrMethod().getMethod().getAnnotations();
    }

    @Override
    public String getMethodName() {
        methodNotNull();
        return method.getConstructorOrMethod().getMethod().getName();
    }

    @Override
    public String getDeclaredClassName() {
        methodNotNull();
        return method.getConstructorOrMethod().getDeclaringClass().getName();
    }

    @Override
    public String getTestClassName() {
        methodNotNull();
        return method.getTestClass().getName();
    }

    @Override
    public String getRealClassName() {
        methodNotNull();
        return method.getRealClass().getName();
    }

    @Override
    public String[] getMethodDependsOnMethods() {
        methodNotNull();
        return method.getMethodsDependedUpon();
    }

    @Override
    public boolean isBeforeClassConfiguration() {
        methodNotNull();
        return method.isBeforeClassConfiguration();
    }

    @Override
    public boolean isAfterClassConfiguration() {
        methodNotNull();
        return method.isAfterClassConfiguration();
    }

    @Override
    public boolean isBeforeTestConfiguration() {
        methodNotNull();
        return method.isBeforeTestConfiguration();
    }

    @Override
    public boolean isAfterTestConfiguration() {
        methodNotNull();
        return method.isAfterTestConfiguration();
    }

    @Override
    public TestAnnotationAdapter getTestAnnotationAdapter() {
        methodNotNull();
        Test testAnnotation = method.getConstructorOrMethod().getMethod().getAnnotation(Test.class);
        return new TestAnnotationAdapterImpl(testAnnotation);
    }

    private void methodNotNull() {
        if(method == null) {
            throw new RuntimeException(ERR_MSG_METHOD_REQUIRED);
        }
    }

}
