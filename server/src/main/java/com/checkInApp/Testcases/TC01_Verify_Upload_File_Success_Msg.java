/**
 * 
 */
package com.checkInApp.Testcases;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.checkInApp.Pages.HomePage;
import com.checkInApp.Utility.BrowserFactory;

/**
 * @author tajr
 *
 */
public class TC01_Verify_Upload_File_Success_Msg 
{
	WebDriver driver;
	HomePage homePage;
	
	
	@BeforeMethod
	public void beforeMethod()
	{
	
	//This will launch the browser and specify URL
	driver=BrowserFactory.startApplication(driver, "Chrome", "https://checkins.objectcomputing.com/upload");
	
	//Created Page Object using Page Factory
	homePage = PageFactory.initElements(driver, HomePage.class);
	
	}
	
	@Test
	public void verifyCheckInAppLaunch() throws InterruptedException
	{
	
	homePage.uploadFile();
	homePage.uploadFileSuccessMsg();
	
	}
	
	@AfterMethod

	  public void afterMethod() {

		BrowserFactory.quitBrowser(driver);

	  }
}
