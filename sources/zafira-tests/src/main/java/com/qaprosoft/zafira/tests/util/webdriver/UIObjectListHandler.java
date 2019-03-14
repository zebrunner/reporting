package com.qaprosoft.zafira.tests.util.webdriver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;

@SuppressWarnings("unused")
public class UIObjectListHandler<T extends AbstractUIObject> implements InvocationHandler
{

	private Class<?> clazz;
	private WebDriver webDriver;
	private final ElementLocator locator;
	private String name;

	public UIObjectListHandler(Class<?> clazz, WebDriver webDriver, ElementLocator locator, String name)
	{
		this.clazz = clazz;
		this.webDriver = webDriver;
		this.locator = locator;
		this.name = name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object invoke(Object object, Method method, Object[] objects) throws Throwable
	{

		List<WebElement> elements = locator.findElements();
		List<T> uIObjects = null;
		if (elements != null)
		{
			uIObjects = elements.stream().map(element -> {
				T uiObject;
				try
				{
					uiObject = (T) clazz.getConstructor(WebDriver.class, SearchContext.class).newInstance(webDriver, element);
					uiObject.setRootElement(element);
				} catch (Exception e)
				{
					throw new RuntimeException("Implement appropriate AbstractUIObject constructor for auto-initialization: "
							+ e.getMessage(), e);
				}
				return uiObject;
			}).collect(Collectors.toList());
		}

		try
		{
			return method.invoke(uIObjects, objects);
		} catch (InvocationTargetException e)
		{
			throw e.getCause();
		}
	}
}
