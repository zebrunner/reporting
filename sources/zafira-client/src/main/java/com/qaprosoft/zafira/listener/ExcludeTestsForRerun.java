package com.qaprosoft.zafira.listener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ITestNGMethod;
import org.testng.SuiteRunner;
import org.testng.TestRunner;
import org.testng.annotations.Test;
import org.testng.internal.Configuration;
import org.testng.internal.TestResult;
import org.testng.xml.XmlSuite;

import com.qaprosoft.zafira.config.IConfigurator;
import com.qaprosoft.zafira.models.dto.TestType;

public class ExcludeTestsForRerun
{
	private final static String DO_NOT_RUN_TEST_NAMES = "doNotRunTestNames";
	private final static String ENABLED = "enabled";

	private static final Logger LOGGER = LoggerFactory.getLogger(ZafiraListener.class);

	public static void excludeTestsForRerun(ISuite suite, List<TestType> testRunResults, IConfigurator configurator)
	{
		List<String> testNamesNoRerun = new ArrayList<>();
		for (TestType test : testRunResults)
		{
			if (!test.isNeedRerun())
			{
				testNamesNoRerun.add(test.getName());
			}
		}
		String[] testNamesNoRerunArr = testNamesNoRerun.toArray(new String[testNamesNoRerun.size()]);
		String[] allDependentMethods = ArrayUtils.EMPTY_STRING_ARRAY;
		for (ITestNGMethod testNGMethod : suite.getAllMethods())
		{
			allDependentMethods = ArrayUtils.addAll(allDependentMethods, testNGMethod.getMethodsDependedUpon());
		}
		boolean isAnythingMarked = true;
		while (isAnythingMarked)
		{
			isAnythingMarked = false;
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
							if (!ArrayUtils.contains(allDependentMethods, testNGMethod.getRealClass().getName() + "."
									+ testNGMethod.getConstructorOrMethod().getMethod().getName()))
							{
								SuiteRunner suiteRunner = new SuiteRunner(new Configuration(), new XmlSuite(), "");
								TestRunner testRunner = new TestRunner(new Configuration(), suiteRunner,
										testNGMethod.getXmlTest(), false, null);
								TestResult testResult = new TestResult(testNGMethod.getTestClass(),
										testNGMethod.getInstance(), testNGMethod, null, 0, 0, testRunner);
								if (testNamesNoRerun.contains(configurator.getTestName(testResult))
										&& ((Test) a).enabled())
								{
									modifyAnnotationValue(a, testNGMethod, ENABLED, false);
									isAnythingMarked = true;

									for (String m : testNGMethod.getMethodsDependedUpon())
									{
										allDependentMethods = ArrayUtils.removeElement(allDependentMethods, m);
									}
								}
							}
						}
						break;
					}
				}
				if (isTest && isDataProviderPresent)
				{
					for (Annotation a : annotations)
					{
						modifyAnnotationValue(a, testNGMethod, DO_NOT_RUN_TEST_NAMES, testNamesNoRerunArr);
					}
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
