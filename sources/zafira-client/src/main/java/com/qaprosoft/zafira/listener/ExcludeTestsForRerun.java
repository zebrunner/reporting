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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.qaprosoft.zafira.listener.adapter.MethodAdapter;
import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestAnnotationAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.listener.adapter.impl.TestResultAdapterImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.zafira.config.IConfigurator;
import com.qaprosoft.zafira.models.dto.TestType;

// todo investigate real business and refactor it
public class ExcludeTestsForRerun {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcludeTestsForRerun.class);

    private final static String DO_NOT_RUN_TEST_NAMES = "doNotRunTestNames";
    private final static String ENABLED = "enabled";

    public static void excludeTestsForRerun(SuiteAdapter adapter, List<TestType> testRunResults, IConfigurator configurator) {
        List<String> testNamesNoRerun = new ArrayList<>();
        Set<String> classesToRerun = new HashSet<>();
        for (TestType test : testRunResults) {
            if (!test.isNeedRerun()) {
                testNamesNoRerun.add(test.getName());
            } else {
                classesToRerun.add(test.getTestClass());
            }
        }
        String[] testNamesNoRerunArr = testNamesNoRerun.toArray(new String[testNamesNoRerun.size()]);
        String[] allDependentMethods = adapter.getSuiteDependsOnMethods();
        boolean isAnythingMarked = true;
        while (isAnythingMarked) {
            isAnythingMarked = false;
            for (MethodAdapter methodAdapter : adapter.getMethodAdapters()) {
                Annotation[] annotations = methodAdapter.getMethodAnnotations();
                boolean isTest = false;
                boolean shouldUpdateDataProvider = false;
                for (Annotation a : annotations) {
                    TestAnnotationAdapter testAnnotationAdapter = methodAdapter.getTestAnnotationAdapter();
                    Class<? extends Annotation> testAnnotationClass = testAnnotationAdapter.getTestAnnotationClass();
                    if (testAnnotationClass != null && testAnnotationClass.isAssignableFrom(a.getClass())) {
                        isTest = true;
                        boolean useDataProvider = StringUtils.isEmpty(methodAdapter.getTestAnnotationAdapter().getDataProviderName());
                        if (!useDataProvider) {
                            boolean classNeedRerun = classesToRerun.contains(methodAdapter.getRealClassName());
                            if (!classNeedRerun && isTestEnabled(testAnnotationAdapter)) {
                                allDependentMethods = skipDependentMethods(allDependentMethods, a, methodAdapter);
                                isAnythingMarked = true;
                            } else {
                                shouldUpdateDataProvider = true;
                            }
                        } else {
                            if (!ArrayUtils.contains(allDependentMethods, methodAdapter.getRealClassName() + "." + methodAdapter.getMethodName())) {
                                boolean testNeedRerun = isTestNeedRerun(methodAdapter, testNamesNoRerun, configurator);
                                if (!testNeedRerun && isTestEnabled(testAnnotationAdapter)) {
                                    skipDependentMethods(allDependentMethods, a, methodAdapter);
                                    isAnythingMarked = true;
                                }
                            }
                        }
                        break;
                    }
                }
                if (isTest && shouldUpdateDataProvider) {
                    for (Annotation a : annotations) {
                        modifyAnnotationValue(a, methodAdapter, DO_NOT_RUN_TEST_NAMES, testNamesNoRerunArr);
                    }
                }
            }
        }

    }

    /**
     * Inserts new value to annotation field by name
     * @param a - annotation to modify
     * @param methodAdapter - method adapter to log annotated method name
     * @param fieldName - annotation field name to search and modify
     * @param newValue - new field value to put
     */
    @SuppressWarnings("unchecked")
    private static void modifyAnnotationValue(Annotation a, MethodAdapter methodAdapter, String fieldName,
            Object newValue) {
        Class<? extends Annotation> c = a.getClass();
        Method[] aMethods = c.getDeclaredMethods();
        for (Method m : aMethods) {
            if (fieldName.equals(m.getName())) {
                LOGGER.info(String.format("'%s' annotation was found for method '%s'", m.getName(), methodAdapter.getMethodName()));

                Object handler = Proxy.getInvocationHandler(a);
                Field f;
                try {
                    f = handler.getClass().getDeclaredField("memberValues");
                } catch (NoSuchFieldException | SecurityException e) {
                    throw new IllegalStateException(e);
                }
                f.setAccessible(true);
                Map<String, Object> memberValues;
                try {
                    memberValues = (Map<String, Object>) f.get(handler);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
                memberValues.put(fieldName, newValue);
                return;
            }
        }
    }

    private static String[] skipDependentMethods(String[] allDependentMethods, Annotation methodAnnotation, MethodAdapter adapter) {
        modifyAnnotationValue(methodAnnotation, adapter, ENABLED, false);
        return removeDependentMethodsFromArray(allDependentMethods, adapter);
    }

    private static boolean isTestEnabled(TestAnnotationAdapter testAnnotationAdapter) {
        return testAnnotationAdapter.isEnabled();
    }

    private static String[] removeDependentMethodsFromArray(String[] allDependentMethods, MethodAdapter adapter) {
        for (String m : adapter.getMethodDependsOnMethods()) {
            allDependentMethods = ArrayUtils.removeElement(allDependentMethods, m);
        }
        return allDependentMethods;
    }

    private static boolean isTestNeedRerun(MethodAdapter adapter, List<String> testNamesNoRerun, IConfigurator configurator) {
        TestResultAdapter testResultAdapter = new TestResultAdapterImpl(adapter);
        return !testNamesNoRerun.contains(configurator.getTestName(testResultAdapter));
    }

}
