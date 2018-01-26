package com.qaprosoft.zafira.tests.util.webdriver;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;

import java.lang.reflect.*;
import java.util.List;

public class UIElementDecorator implements FieldDecorator
{

    private WebDriver driver;
    private ElementLocatorFactory factory;

    public UIElementDecorator(WebDriver driver, ElementLocatorFactory factory) {
        this.driver = driver;
        this.factory = factory;
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) {
        if(! field.isAnnotationPresent(FindBy.class) || (! AbstractUIObject.class.isAssignableFrom(field.getType()) && !isList(field)
                && ! WebElement.class.isAssignableFrom(field.getType()))) {
            return null;
        }
        ElementLocator locator = factory.createLocator(field);
        if(locator == null) {
            return null;
        }
        if(WebElement.class.isAssignableFrom(field.getType()) || (isList(field) && WebElement.class.isAssignableFrom(getListType(field).getClass()))) {
            return (new DefaultFieldDecorator(this.factory).decorate(loader, field));
        }
        return isList(field) ? getObjects(loader, field, locator) : getObject(loader, field, locator);
    }

    private <T extends AbstractUIObject> T getObject(ClassLoader loader, Field field, ElementLocator locator) {
        Class<? extends AbstractUIObject> clazz = (Class<? extends AbstractUIObject>) field.getType();
        T uiObject;
        WebElement proxy = null;
        try
        {
            proxy = (WebElement) Proxy.newProxyInstance(loader, new Class[]
                    { WebElement.class, WrapsElement.class, Locatable.class }, new LocatingElementHandler(locator));
            uiObject = (T) clazz.getConstructor(WebDriver.class, SearchContext.class).newInstance(driver, proxy);
        } catch (NoSuchMethodException e)
        {
            throw new RuntimeException(
                    "Implement appropriate AbstractUIObject constructor for auto-initialization: "
                            + e.getMessage(), e);
        } catch (Exception e)
        {
            throw new RuntimeException("Error creating UIObject: " + e.getMessage(), e);
        }
        uiObject.setRootElement(proxy);
        return uiObject;
    }

    protected <T extends AbstractUIObject> List<T> getObjects(ClassLoader loader, Field field, ElementLocator locator) {
        InvocationHandler handler = new UIObjectListHandler<>((Class<?>) getListType(field), driver, locator, field.getName());
        List<T> proxies = (List<T>) Proxy.newProxyInstance(loader, new Class[]{ List.class }, handler);
        return proxies;
    }

    private Type getListType(Field field) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return null;
        }
        return ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }

    private boolean isList(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }
}
