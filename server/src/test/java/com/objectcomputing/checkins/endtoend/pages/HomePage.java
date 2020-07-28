/**
 * 
 */
package com.objectcomputing.checkins.endtoend.pages;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author tajr
 * 
 * This class will store all the locators and methods of home page.
 *
 */
public class HomePage {
	
	WebDriver driver;
	
	public HomePage(WebDriver ldriver)
	{
		this.driver = ldriver;
	}
	
	@FindBy(xpath="/html[1]/body[1]/div[1]/form[1]/fieldset[1]/label[1]") 
	WebElement chooseafile_btn;
	
	@FindBy(id="file")
	WebElement filepath;
	
	@FindBy(name="submit")
	WebElement upload_btn;

	@FindBy(className="success")
	WebElement success_msg;
	
	@FindBy(className="error")
	WebElement error_msg;
	
	
	public void uploadFile() throws InterruptedException
	{
		filepath.sendKeys("/Users/tajr/Desktop/File.rtf");
		Thread.sleep(5000);
		upload_btn.click();
		Thread.sleep(5000);
	}
	
	public void uploadFileSuccessMsg() throws InterruptedException
	{
		String successMsg = success_msg.getText();
		System.out.println("Upload File Msg: " +successMsg);
		Thread.sleep(1000);	
		assertEquals(success_msg.getText(),"THE FILE FILE.RTF WAS UPLOADED");
		Thread.sleep(5000);
	}
		
	public void uploadFileErrorMsg() throws InterruptedException
	{
		upload_btn.click();
		Thread.sleep(5000);
		String errorMsg = error_msg.getText();
		System.out.println("Upload File Msg: " +errorMsg);
		Thread.sleep(1000);	
		assertEquals(error_msg.getText(),"Please select a file before uploading.");
		Thread.sleep(5000);
	}

}
