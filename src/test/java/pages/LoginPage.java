package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    WebDriver driver;

    By usernameField = By.xpath("//input[@placeholder='Username']");
    By passwordField = By.xpath("//input[@placeholder='Password']");
    By loginButton = By.xpath("//button[normalize-space()='Login']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login() {
        driver.findElement(usernameField).click();
        driver.findElement(usernameField).sendKeys("kevin.khokhani");

        driver.findElement(passwordField).click();
        driver.findElement(passwordField).sendKeys("Test@123");

        driver.findElement(loginButton).click();
    }
}