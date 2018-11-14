package com.qaprosoft.zafira.tests.gui.components.daterangepicker;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;

public class DatePickerContainer extends AbstractUIObject
{

    @FindBy(xpath = ".//*[@event-key = 'prev']")
    private WebElement prevButton;

    @FindBy(xpath = ".//*[@placeholder = 'Month']")
    private WebElement monthSelect;

    @FindBy(xpath = ".//*[@placeholder = 'Year']")
    private WebElement yearSelect;

    @FindBy(xpath = ".//*[@event-key = 'next']")
    private WebElement nextButton;

    @FindBy(css = ".md-date-range-picker__calendar__selection")
    private List<WebElement> dates;

    public DatePickerContainer(WebDriver driver)
    {
        super(driver);
    }

    public WebElement getPrevButton()
    {
        return prevButton;
    }

    public void clickPrevButton()
    {
        prevButton.click();
    }

    public WebElement getMonthSelect()
    {
        return monthSelect;
    }

    public void selectMonth(String month)
    {
        select(monthSelect, month);
    }

    public WebElement getYearSelect()
    {
        return yearSelect;
    }

    public void selectYear(String year)
    {
        select(yearSelect, year);
    }

    public WebElement getNextButton()
    {
        return nextButton;
    }

    public void clickNextButton()
    {
        nextButton.click();
    }

    public List<WebElement> getDates()
    {
        return dates;
    }

    public void clickDateByValue(String value)
    {
        dates.stream().filter(date -> date.getText().equals(value)).findFirst().ifPresent(WebElement::click);
    }
}
