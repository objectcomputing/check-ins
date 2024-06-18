/**
 *
 */
package com.objectcomputing.checkins.endtoend.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author tajr
 *
 * This class will store all the locators and methods of home page.
 *
 */
public class HomePage {

    WebDriver driver;
    @FindBy(xpath = "/html[1]/body[1]/div[1]/form[1]/fieldset[1]/label[1]")
    WebElement chooseAFileBtn;
    @FindBy(id = "file")
    WebElement filepath;
    @FindBy(name = "submit")
    WebElement uploadBtn;
    @FindBy(className = "success")
    WebElement successMsg;
    @FindBy(className = "error")
    WebElement errorMsg;

    public HomePage(WebDriver ldriver) {
        this.driver = ldriver;
    }

    public void uploadFileSuccessMsg() throws InterruptedException {
        String successMsg = this.successMsg.getText();
        System.out.println("Upload File Msg: " + successMsg);
        Thread.sleep(1000);
        assertEquals("THE FILE APPLICATION.YML WAS UPLOADED", this.successMsg.getText());
        Thread.sleep(5000);
    }


	public void uploadFile() throws InterruptedException
	{
		File file = getFileFromResources("application.yml");
		filepath.sendKeys(file.getPath());
		Thread.sleep(5000);
		uploadBtn.click();
		Thread.sleep(5000);
	}

	private File getFileFromResources(String fileName) {

		ClassLoader classLoader = getClass().getClassLoader();

		URL resource = classLoader.getResource(fileName);
		if (resource == null) {
			throw new IllegalArgumentException("file is not found!");
		} else {
			return new File(resource.getFile());
		}

	}
    public void uploadFileErrorMsg() throws InterruptedException {
        uploadBtn.click();
        Thread.sleep(5000);
        String errorMsg = this.errorMsg.getText();
        System.out.println("Upload File Msg: " + errorMsg);
        Thread.sleep(1000);
        assertEquals("Please select a file before uploading.", this.errorMsg.getText());
        Thread.sleep(5000);
    }

}
