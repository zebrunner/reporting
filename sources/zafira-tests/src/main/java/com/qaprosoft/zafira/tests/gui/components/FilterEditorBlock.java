package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Arrays;
import java.util.List;

public class FilterEditorBlock extends AbstractUIObject
{

    @FindBy(id = "filterName")
    private WebElement filterNameInput;

    @FindBy(id = "filterDescription")
    private WebElement filterDescriptionInput;

    @FindBy(id = "filterState")
    private WebElement filterStateCheckbox;

    @FindBy(id = "filterCriteria")
    private WebElement filterCriteriaSelect;

    @FindBy(id = "filterOperator")
    private WebElement filterOperatorSelect;

    @FindBy(xpath = ".//input[@placeholder = 'Value']")
    private WebElement filterValueInput;

    @FindBy(xpath = ".//md-select[@placeholder = 'Value']")
    private WebElement filterValueSelect;

    @FindBy(id = "add-button")
    private WebElement filterCriteriaAddButton;

    @FindBy(css = ".filter-chip-scope md-chip")
    private List<Chip> filterChipScope;

    public FilterEditorBlock(WebDriver driver, SearchContext context)
    {
        super(driver, context);
    }

    public WebElement getFilterNameInput()
    {
        return filterNameInput;
    }

    public void typeFilterName(String name)
    {
        clearInput(filterNameInput);
        filterNameInput.sendKeys(name);
    }

    public WebElement getFilterDescriptionInput()
    {
        return filterDescriptionInput;
    }

    public void typeFilterDescription(String description)
    {
        clearInput(filterDescriptionInput);
        filterDescriptionInput.sendKeys(description);
    }

    public WebElement getFilterStateCheckbox()
    {
        return filterStateCheckbox;
    }

    public void clickFilterStateCheckbox()
    {
        filterStateCheckbox.click();
    }

    public WebElement getFilterCriteriaSelect()
    {
        return filterCriteriaSelect;
    }

    public void selectCriteria(String criteria)
    {
        select(filterCriteriaSelect, criteria);
    }

    public WebElement getFilterOperatorSelect()
    {
        return filterOperatorSelect;
    }

    public void selectOperator(String operator)
    {
        select(filterOperatorSelect, operator);
    }

    public WebElement getFilterValueInput()
    {
        return filterValueInput;
    }

    public void typeFilterValue(String value)
    {
        filterValueInput.sendKeys(value);
    }

    public WebElement getFilterValueSelect()
    {
        return filterValueSelect;
    }

    public void selectFilterValue(String value)
    {
        select(filterValueSelect, value);
    }

    public WebElement getFilterCriteriaAddButton()
    {
        return filterCriteriaAddButton;
    }

    public void clickFilterCriteriaAddButton()
    {
        filterCriteriaAddButton.click();
    }

    public List<Chip> getFilterChipScope()
    {
        return filterChipScope;
    }

    public Chip getFilterScopeChipByName(String name)
    {
        return filterChipScope.stream()
                .filter(chip -> chip.getContentText(false).equals(name))
                .findFirst().orElse(null);
    }

    public void createCriterias(String... criterias)
    {
        Arrays.asList(criterias).forEach(criteria -> {
            String[] criteriaItems = criteria.split(":");
            waitUntilElementToBeClickableWithBackdropMask(filterCriteriaSelect, 2);
            selectCriteria(criteriaItems[0].toLowerCase());
            pause(0.2);
            selectOperator(criteriaItems[1].toLowerCase());
            pause(0.2);
            if(criteriaItems.length == 3)
            {
                if(isElementPresent(filterValueInput, 2))
                {
                    typeFilterValue(criteriaItems[2].toLowerCase());
                } else if(isElementPresent(filterValueSelect, 2)) {
                    selectFilterValue(criteriaItems[2].toLowerCase());
                }
            }
            pause(0.6);
            clickFilterCriteriaAddButton();
        });
    }
}
