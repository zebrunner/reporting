package com.qaprosoft.zafira.tests.services.gui;

import com.qaprosoft.zafira.tests.exceptions.NoColorSchemaIsChosenException;
import com.qaprosoft.zafira.tests.gui.pages.UserProfilePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UserProfilePageService extends AbstractPageService
{

	private UserProfilePage userProfilePage;

	public UserProfilePageService(WebDriver driver)
	{
		super(driver);
		this.userProfilePage = new UserProfilePage(driver);
	}

	public UserProfilePage.ColorSchema checkCurrentColorSchemeByRadioButton(){
		userProfilePage.waitUntilPageIsLoaded(4);
		if(userProfilePage.getLightZafiraSchemaRadioButtonChecked() != null){
			return UserProfilePage.ColorSchema.LIGHT;
		}
		else if (userProfilePage.getDarkZafiraSchemaRadioButtonChecked() != null){
			return UserProfilePage.ColorSchema.DARK;
		}
		else {
			throw new NoColorSchemaIsChosenException("No radioButton corresponding to the schema choice was found");
		}
	}

	public void pickDarkSchemaRadioButton(){
		userProfilePage.getDarkZafiraSchemaRadioButton().click();
	}

	public void pickLightSchemaRadioButton(){
		userProfilePage.getLightZafiraSchemaRadioButton().click();
	}

	public boolean darkSchemaStyleIsDisplayed(){
		return userProfilePage.getDarkZafiraSchemaStyle() != null;
	}

	public boolean lightSchemaStyleIsDisplayed(){
		return userProfilePage.getLightZafiraSchemaStyle() != null;
	}

	public void generateToken()
	{
		userProfilePage.waitUntilElementIsPresent(userProfilePage.getGenerateTokenButton(), 3);
		userProfilePage.getGenerateTokenButton().click();
		userProfilePage.waitUntilElementIsPresent(userProfilePage.getCopyTokenButton(), 1);
	}

	public boolean copyToken()
	{
		new WebDriverWait(driver, 5).until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//header/div[contains(@class, 'profile-img')]")));
		userProfilePage.getGenerateTokenButton().click();
		new WebDriverWait(driver, 1).until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//button[ancestor::form[@name='access_token_form'] and @type='button']")));
		userProfilePage.getCopyTokenButton().click();
		return userProfilePage.getCopyActionAlert() != null;
	}
}
