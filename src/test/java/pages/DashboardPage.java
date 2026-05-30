package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DashboardPage {

    WebDriver driver;
    WebDriverWait wait;

    By complianceMenu = By.xpath("//span[normalize-space()='Compliance'] | //a[contains(.,'Compliance')]");
    By coursesMenu = By.xpath("//span[normalize-space()='Courses'] | //a[contains(.,'Courses')]");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void openCoursesModule() {

        WebElement compliance = wait.until(ExpectedConditions.presenceOfElementLocated(complianceMenu));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", compliance);

        WebElement courses = wait.until(ExpectedConditions.presenceOfElementLocated(coursesMenu));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", courses);
    }
}