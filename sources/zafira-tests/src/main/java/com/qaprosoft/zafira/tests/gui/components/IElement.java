package com.qaprosoft.zafira.tests.gui.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface IElement
{

	By getLocator();

	WebElement getElement();

	default WebElement findElement(By by)
	{
		return getElement().findElement(by);
	}

	default List<WebElement> findElements(By by)
	{
		return getElement().findElements(by);
	}
}
