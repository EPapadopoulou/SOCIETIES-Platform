package org.societies.webapp.integration.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProfileSettingsEditConditionDialog extends BasePageComponent {
    private static final String DIALOG_XPATH = "//*[@id='mainForm:editConditionDialog']";
    private static final String SAVE_BTN_XPATH = "//*[@id='mainForm:editConditionDialog']//*[contains(@class, 'ui-button-text') and text()='Save']";
    private static final String CANCEL_BTN_XPATH = "//*[@id='mainForm:editConditionDialog']//*[contains(@class, 'ui-button-text') and text()='Cancel']";
    private static final String NAME_FIELD_XPATH = "//*[@id='mainForm:conditionName']";
    private static final String VALUE_FIELD_XPATH = "//*[@id='mainForm:conditionValue']";

    public ProfileSettingsEditConditionDialog(WebDriver driver) {
        super(driver);
        waitUntilVisible(By.xpath(DIALOG_XPATH));
    }

    public void clickSave() {
        clickButton(By.xpath(SAVE_BTN_XPATH));
        waitUntilNotVisible(By.xpath(DIALOG_XPATH));
    }

    public void clickCancel() {
        clickButton(By.xpath(CANCEL_BTN_XPATH));
        waitUntilNotVisible(By.xpath(DIALOG_XPATH));
    }

    public void setName(String value) {
        setFieldValue(value, By.xpath(NAME_FIELD_XPATH));
    }

    public void setOperator(String value) {
//        super.pickDropdownValue(value, By.xpath)
    }

    public void setValue(String value) {
        setFieldValue(value, By.xpath(VALUE_FIELD_XPATH));
    }
}
