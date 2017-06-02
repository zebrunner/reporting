package com.qaprosoft.zafira.listener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ITestNGMethod;
import org.testng.TestRunner;
import org.testng.annotations.Test;
import org.testng.internal.Configuration;
import org.testng.internal.TestResult;

import com.qaprosoft.zafira.config.IConfigurator;
import com.qaprosoft.zafira.models.dto.TestType;

public class ExcludeTestsForRerun
{
	private final static String DO_NOT_RUN_TEST_NAMES = "doNotRunTestNames";
	private final static String ENABLED = "enabled";

	private static final Logger LOGGER = LoggerFactory.getLogger(ZafiraListener.class);

	public static void excludeTestsForRerun(ISuite suite, List<TestType> testRunResults, IConfigurator configurator)
	{
		List<String> testNames2rerun = new ArrayList<>();
		for (TestType test : testRunResults)
		{
			if (test.isNeedRerun())
			{
				testNames2rerun.add(test.getName());
			}
		}
		String[] testNames2rerunArr = testNames2rerun.toArray(new String[testNames2rerun.size()]);
		for (ITestNGMethod testNGMethod : suite.getAllMethods())
		{
			Annotation[] annotations = testNGMethod.getConstructorOrMethod().getMethod().getAnnotations();
			boolean isDataProviderPresent = false;
			boolean isTest = false;
			for (Annotation a : annotations)
			{
				if (a instanceof Test)
				{
					isTest = true;
					if (((Test) a).dataProvider() != null && !((Test) a).dataProvider().isEmpty())
					{
						isDataProviderPresent = true;
					} else
					{
						TestRunner testRunner = new TestRunner(new Configuration(), suite, testNGMethod.getXmlTest(),
								false, null);
						TestResult testResult = new TestResult(testNGMethod.getTestClass(), testNGMethod.getInstance(),
								testNGMethod, null, 0, 0, testRunner);
						if (testNames2rerun.contains(configurator.getTestName(testResult)))
						{
							modifyAnnotationValue(a, testNGMethod, ENABLED, false);
						}
					}
					break;
				}
			}
			if (isTest && isDataProviderPresent)
			{
				for (Annotation a : annotations)
				{
					modifyAnnotationValue(a, testNGMethod, DO_NOT_RUN_TEST_NAMES, testNames2rerunArr);
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	private static void modifyAnnotationValue(Annotation a, ITestNGMethod testNGMethod, String fieldName,
			Object newValue)
	{
		Class<? extends Annotation> c = a.getClass();
		Method[] aMethods = c.getDeclaredMethods();
		for (Method m : aMethods)
		{
			if (fieldName.equals(m.getName()))
			{
				LOGGER.info(String.format("'%s' annotation was found for method '%s'", m.getName(),
						testNGMethod.getConstructorOrMethod().getMethod().getName()));

				Object handler = Proxy.getInvocationHandler(a);
				Field f;
				try
				{
					f = handler.getClass().getDeclaredField("memberValues");
				} catch (NoSuchFieldException | SecurityException e)
				{
					throw new IllegalStateException(e);
				}
				f.setAccessible(true);
				Map<String, Object> memberValues;
				try
				{
					memberValues = (Map<String, Object>) f.get(handler);
				} catch (IllegalArgumentException | IllegalAccessException e)
				{
					throw new IllegalStateException(e);
				}
				memberValues.put(fieldName, newValue);
				return;
			}
		}
	}

}
