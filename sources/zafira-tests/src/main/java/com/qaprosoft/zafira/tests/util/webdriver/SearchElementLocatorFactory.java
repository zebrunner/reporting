package com.qaprosoft.zafira.tests.util.webdriver;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

public class SearchElementLocatorFactory implements ElementLocatorFactory
{

    private final SearchContext context;

    public SearchElementLocatorFactory(SearchContext context) {
        this.context = context;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        return new DefaultElementLocator(context, field);
    }
}
