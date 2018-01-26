package com.qaprosoft.zafira.tests.util.webdriver;

import com.qaprosoft.zafira.tests.gui.components.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class UIObjectListHandler<T extends AbstractUIObject> implements InvocationHandler {

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
    public Object invoke(Object object, Method method, Object[] objects) throws Throwable
    {

        List<WebElement> elements = locator.findElements();
        List<T> uIObjects = new ArrayList<T>();
        int index = 0;
        if (elements != null)
        {
            for (WebElement element : elements)
            {
                T uiObject;
                try
                {
                    uiObject = (T) clazz.getConstructor(WebDriver.class, SearchContext.class)
                            .newInstance(
                                    webDriver, element);
                } catch (NoSuchMethodException e)
                {
                    System.out.println("Implement appropriate AbstractUIObject constructor for auto-initialization: "
                            + e.getMessage());
                    throw new RuntimeException(
                            "Implement appropriate AbstractUIObject constructor for auto-initialization: "
                                    + e.getMessage(), e);
                }
                uIObjects.add(uiObject);
            }
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
