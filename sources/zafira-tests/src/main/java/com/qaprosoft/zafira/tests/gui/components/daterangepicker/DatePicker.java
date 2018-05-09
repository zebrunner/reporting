package com.qaprosoft.zafira.tests.gui.components.daterangepicker;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class DatePicker extends AbstractUIObject
{

    @FindBy(tagName = "md-date-range-picker")
    private DatePickerContainer datePickerContainer;

    protected DatePicker(WebDriver driver)
    {
        super(driver);
    }

    public DatePickerContainer getDatePickerContainer()
    {
        return datePickerContainer;
    }

    public DatePickerContainer open()
    {
        rootElement.click();
        return new DatePickerContainer(driver);
    }
}
