package com.objectcomputing.checkins.endtoend.utility;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.concurrent.TimeUnit;

public class BrowserFactory {


    public static WebDriver startApplication(WebDriver driver, String browserName, String appURL) {
        if (browserName.equals("Chrome")) {
            WebDriverManager.chromedriver().setup();
            //Initiating your chromedriver
            ChromeOptions options = new ChromeOptions();
            String opts = System.getenv("CHROME_OPTS");
            if (opts != null) {
                options.addArguments(opts);
            }
            driver = new ChromeDriver(options);

        } else if (browserName.equals("Firefox")) {
            WebDriverManager.firefoxdriver().setup();
            //Initiating your firefoxdriver
            FirefoxOptions options = new FirefoxOptions();
            String opts = System.getenv("FIREFOX_OPTS");
            if (opts != null) {
                options.addArguments(opts);
            }
            driver = new FirefoxDriver(options);
        }
		/*else if(browserName.equals("IE"))
		{
			
		}*/
        else {
            System.out.println("We do not support this browser");
        }
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

        driver.manage().window().maximize();

        driver.get(appURL);

        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        return driver;
    }

    public static void quitBrowser(WebDriver driver) {
        driver.quit();
    }
} 
