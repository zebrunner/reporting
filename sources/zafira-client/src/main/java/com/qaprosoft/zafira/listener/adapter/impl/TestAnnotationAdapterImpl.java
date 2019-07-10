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

import com.qaprosoft.zafira.listener.adapter.TestAnnotationAdapter;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;

public class TestAnnotationAdapterImpl implements TestAnnotationAdapter {

    private static final String ERR_MSG_ANNOTATION_REQUIRED = "TestNG test annotation is required to apply its data";

    private final Test annotation;

    public TestAnnotationAdapterImpl(Test annotation) {
        this.annotation = annotation;
    }

    @Override
    public Class<? extends Annotation> getTestAnnotationClass() {
        return Test.class;
    }

    @Override
    public String getDataProviderName() {
        annotationNotNull();
        return annotation.dataProvider();
    }

    @Override
    public boolean isEnabled() {
        annotationNotNull();
        return annotation.enabled();
    }

    private void annotationNotNull() {
        if(annotation == null) {
            throw new RuntimeException(ERR_MSG_ANNOTATION_REQUIRED);
        }
    }

}
