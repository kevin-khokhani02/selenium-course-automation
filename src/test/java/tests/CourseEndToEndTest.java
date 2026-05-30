package tests;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.CoursePage;
import pages.DashboardPage;
import pages.LoginPage;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CourseEndToEndTest extends BaseTest {

    @Test
    public void createCourseEndToEndFlow() throws IOException {

        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        CoursePage coursePage = new CoursePage(driver);

        // File paths
        String documentPath = testDocumentPath();
        String imagePath = testImagePath();
        String courseName = System.getProperty("course.name", uniqueCourseName());

        // Login
        loginPage.login();

        // Open Course Module
        dashboardPage.openCoursesModule();

        // Create course, search it from Manage Courses, open Edit, and finish.
        coursePage.createCourseThenOpenEditAndFinish(courseName, documentPath, imagePath);
    }

    private String uniqueCourseName() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        return "1st Course 30-05-2026 (Document) " + timestamp;
    }

    private String testDocumentPath() throws IOException {
        Path document = Paths.get("target", "test-upload-files", "sample-document.pdf");
        Files.createDirectories(document.getParent());

        if (Files.notExists(document)) {
            String pdfContent = "%PDF-1.4\n" +
                    "1 0 obj\n" +
                    "<< /Type /Catalog /Pages 2 0 R >>\n" +
                    "endobj\n" +
                    "2 0 obj\n" +
                    "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n" +
                    "endobj\n" +
                    "3 0 obj\n" +
                    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 300 144] >>\n" +
                    "endobj\n" +
                    "trailer\n" +
                    "<< /Root 1 0 R >>\n" +
                    "%%EOF\n";
            Files.write(document, pdfContent.getBytes(StandardCharsets.UTF_8));
        }

        return document.toAbsolutePath().toString();
    }

    private String testImagePath() throws IOException {
        Path image = Paths.get("target", "test-upload-files", "course-image.png");
        Files.createDirectories(image.getParent());

        if (Files.notExists(image)) {
            BufferedImage bufferedImage = new BufferedImage(320, 180, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setColor(new Color(0, 128, 128));
            graphics.fillRect(0, 0, 320, 180);
            graphics.setColor(Color.WHITE);
            graphics.drawString("Course Image", 110, 90);
            graphics.dispose();
            ImageIO.write(bufferedImage, "png", image.toFile());
        }

        return image.toAbsolutePath().toString();
    }
}
