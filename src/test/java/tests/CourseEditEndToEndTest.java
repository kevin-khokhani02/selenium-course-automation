package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CoursePage;
import pages.DashboardPage;
import pages.LoginPage;

public class CourseEditEndToEndTest extends BaseTest {

    @Test
    public void editCourseEndToEndFlow() {

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        CoursePage coursePage = new CoursePage(driver);

        String existingCourseName = System.getProperty("course.name", "1st Course 30-05-2026 (Document)");
        String updatedCourseName = System.getProperty(
                "course.updatedName",
                "AutomationEdited" + System.currentTimeMillis()
        );

        loginPage.login();
        dashboardPage.openCoursesModule();

        coursePage.editCourse(existingCourseName, updatedCourseName);

        Assert.assertTrue(
                coursePage.isCourseDisplayed(updatedCourseName),
                "Updated course was not found in Manage Courses: " + updatedCourseName
        );
    }
}
