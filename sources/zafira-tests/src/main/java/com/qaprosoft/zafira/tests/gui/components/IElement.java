package com.qaprosoft.zafira.tests.gui.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface IElement
{

	By getLocator();

	WebElement getElement();
}
