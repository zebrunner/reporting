package com.qaprosoft.zafira.tests.gui.components;

import com.qaprosoft.zafira.tests.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class FilterBlock extends AbstractUIObject
{

    @FindBy(className = "manage-chip")
    private WebElement manageChip;

    @FindBy(xpath = ".//*[@class = 'manage-chip']/following-sibling::md-chips//md-chip")
    private List<Chip> filterCreatedChips;

    @FindBy(id = "filter-editor")
    private FilterEditorBlock filterEditorBlock;

    public FilterBlock(WebDriver driver, SearchContext context)
    {
        super(driver, context);
    }

    public WebElement getManageChip()
    {
        return manageChip;
    }

    public FilterEditorBlock clickManageChip()
    {
        manageChip.click();
        return filterEditorBlock;
    }

    public List<Chip> getFilterCreatedChips()
    {
        return filterCreatedChips;
    }

    public Chip getFilterCreatedChipByName(String name)
    {
        return filterCreatedChips.stream()
                .filter(chip -> chip.getContentText(false).equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public FilterEditorBlock getFilterEditorBlock()
    {
        return filterEditorBlock;
    }

    public void createFilter(String name, String description, String... criterias)
    {
        manageChip.click();
        filterEditorBlock.typeFilterName(name);
        filterEditorBlock.typeFilterDescription(description);
        pause(0.2);
        filterEditorBlock.createCriterias(criterias);
    }
}
