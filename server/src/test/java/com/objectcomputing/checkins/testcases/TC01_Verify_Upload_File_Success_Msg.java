/**
 * 
 */
package com.objectcomputing.checkins.testcases;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.objectcomputing.checkins.endtoend.pages.HomePage;
import com.objectcomputing.checkins.endtoend.utility.BrowserFactory;

/**
 * @author tajr
 *
 */
public class TC01_Verify_Upload_File_Success_Msg 
{
	WebDriver driver;
	HomePage homePage;
	
	
	@Before
	public void beforeMethod()
	{
	
	//This will launch the browser and specify URL
	driver=BrowserFactory.startApplication(driver, "Chrome", "https://checkins.objectcomputing.com/upload");
	
	//Created Page Object using Page Factory
	homePage = PageFactory.initElements(driver, HomePage.class);
	
	}
	
	@Test
	public void testVerifyCheckInAppLaunch() throws InterruptedException
	{
	homePage.uploadFile();
	homePage.uploadFileSuccessMsg();
	
	}
	
	@After

	  public void afterMethod() {

		BrowserFactory.quitBrowser(driver);

	  }
}
