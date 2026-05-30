package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CoursePage {

    WebDriver driver;
    WebDriverWait wait;

    By actionDropdown = By.xpath("//button[contains(.,'Action')]");
    By manageCourses = By.xpath("//a[normalize-space()='Manage Courses']");
    By addCourseButton = By.cssSelector("a.btn-add-nav[title='Add Course'][href*='/Course/Add']");

    By nameField = By.xpath("(//label[contains(normalize-space(),'Name')]/following::input[1])[1]");
    By courseSearchField = By.xpath(
            "//label[normalize-space()='Search']/following::input[contains(@placeholder,'Enter Name')][1]"
    );
    By courseSearchButton = By.xpath(
            "//label[normalize-space()='Search']/ancestor::*[contains(@class,'row') or contains(@class,'form')][1]" +
                    "/following::*[self::button or self::a][.//*[contains(@class,'search')] or contains(@class,'btn')][1]"
    );
    By fileTypeDropdown = By.cssSelector("span[role='combobox'][aria-controls='CourseFileTypeId_listbox']");
    By audioVideoDocumentOption = By.xpath(
            "//*[@id='CourseFileTypeId_listbox']//*[self::li or self::span]" +
                    "[contains(normalize-space(), 'Audio / Video / Document')]"
    );
    By fileUpload = By.id("CourseAudioFileId");
    By imageUpload = By.id("PhotoId");
    By finishButton = By.id("btnFinish");
    By confirmationYesButton = By.cssSelector(".sweet-alert.visible button.confirm");

    public CoursePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void clickManageCourses() {
        try {
            WebElement action = wait.until(ExpectedConditions.elementToBeClickable(actionDropdown));
            action.click();

            WebElement manage = wait.until(ExpectedConditions.elementToBeClickable(manageCourses));
            manage.click();
        } catch (Exception e) {
            driver.get("https://staging.sentrient.online/Course/Manage");
        }
    }

    public void clickAddCourse() {
        WebElement add = wait.until(ExpectedConditions.visibilityOfElementLocated(addCourseButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", add);
        wait.until(ExpectedConditions.presenceOfElementLocated(nameField));
    }

    public void enterCourseName() {
        enterCourseName("1st Course 30-05-2026 (Document)");
    }

    public void enterCourseName(String courseName) {
        WebElement name = wait.until(ExpectedConditions.presenceOfElementLocated(nameField));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", name);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value=arguments[1];", name, courseName);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", name
        );
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", name
        );
    }

    public void selectFileTypeAudioVideoDocument() {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(fileTypeDropdown));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", dropdown);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);

        WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(audioVideoDocumentOption));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                fileTypeDropdown,
                "Audio / Video / Document"
        ));
    }

    public void uploadDocumentFile(String documentPath) {
        WebElement upload = wait.until(ExpectedConditions.presenceOfElementLocated(fileUpload));
        makeFileInputReady(upload);
        upload.sendKeys(documentPath);
        waitForKendoUploadToReceiveFile(upload, documentPath);
    }

    public void uploadImageFile(String imagePath) {
        WebElement upload = wait.until(ExpectedConditions.presenceOfElementLocated(imageUpload));
        makeFileInputReady(upload);
        upload.sendKeys(imagePath);
        waitForKendoUploadToReceiveFile(upload, imagePath);
    }

    public void clickFinishButton() {
        WebElement finish = wait.until(ExpectedConditions.visibilityOfElementLocated(finishButton));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", finish);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", finish);
        clickYesOnConfirmationIfDisplayed();
    }

    private void clickYesOnConfirmationIfDisplayed() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(confirmationYesButton));
            shortWait.until(driver -> {
                Object clicked = ((JavascriptExecutor) driver).executeScript(
                        "const yes = document.querySelector('.sweet-alert.visible button.confirm');" +
                                "if (!yes) { return false; }" +
                                "yes.click();" +
                                "return true;"
                );
                return Boolean.TRUE.equals(clicked);
            });
            shortWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".sweet-alert.visible")));
        } catch (Exception ignored) {
            // Some Finish actions do not show a confirmation dialog.
        }
    }

    private void makeFileInputReady(WebElement upload) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});" +
                        "arguments[0].classList.remove('k-hidden');" +
                        "arguments[0].removeAttribute('aria-hidden');" +
                        "arguments[0].removeAttribute('tabindex');" +
                        "arguments[0].style.display = 'block';" +
                        "arguments[0].style.visibility = 'visible';" +
                        "arguments[0].style.opacity = 1;" +
                        "arguments[0].style.position = 'relative';" +
                        "arguments[0].style.width = '1px';" +
                        "arguments[0].style.height = '1px';",
                upload
        );
    }

    private void waitForKendoUploadToReceiveFile(WebElement upload, String filePath) {
        String fileName = filePath.substring(Math.max(filePath.lastIndexOf('\\'), filePath.lastIndexOf('/')) + 1);

        wait.until(driver -> (Boolean) ((JavascriptExecutor) driver).executeScript(
                "const input = arguments[0];" +
                        "const expectedFileName = arguments[1];" +
                        "const wrapper = input.closest('.k-upload');" +
                        "return input.files.length > 0 ||" +
                        "  (wrapper && (wrapper.innerText || '').includes(expectedFileName));",
                upload,
                fileName
        ));
    }

    public void createCourse(String documentPath, String imagePath) {
        clickManageCourses();
        clickAddCourse();
        enterCourseName();
        selectFileTypeAudioVideoDocument();
        uploadDocumentFile(documentPath);
        uploadImageFile(imagePath);
        clickFinishButton();
    }

    public void createCourse(String courseName, String documentPath, String imagePath) {
        clickManageCourses();
        clickAddCourse();
        enterCourseName(courseName);
        selectFileTypeAudioVideoDocument();
        uploadDocumentFile(documentPath);
        uploadImageFile(imagePath);
        clickFinishButton();
    }

    public void createCourseThenOpenEditAndFinish(String courseName, String documentPath, String imagePath) {
        createCourse(courseName, documentPath, imagePath);
        clickManageCourses();
        searchCourse(courseName);
        clickEditForCourse(courseName);
        clickFinishButton();
    }

    public void editCourse(String existingCourseName, String updatedCourseName) {
        clickManageCourses();
        searchCourse(existingCourseName);
        clickEditForCourse(existingCourseName);
        enterCourseName(updatedCourseName);
        clickFinishButton();
        clickManageCourses();
    }

    public void searchCourse(String courseName) {
        openCourseSearchFilter();

        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(courseSearchField));
        search.clear();
        search.sendKeys(courseName);

        clickCourseSearchButton();

        wait.until(ExpectedConditions.visibilityOfElementLocated(courseRowLocator(courseName)));
    }

    public boolean isCourseDisplayed(String courseName) {
        try {
            searchCourse(courseName);
            return driver.findElement(courseNameLocator(courseName)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void clickEditForCourse(String courseName) {
        wait.until(driver -> {
            Object clicked = ((JavascriptExecutor) driver).executeScript(
                    "const courseName = arguments[0];" +
                            "const visible = element => {" +
                            "  const rect = element.getBoundingClientRect();" +
                            "  const style = window.getComputedStyle(element);" +
                            "  return rect.width > 0 && rect.height > 0 && style.display !== 'none' && style.visibility !== 'hidden';" +
                            "};" +
                            "const rows = Array.from(document.querySelectorAll('tr'));" +
                            "const row = rows.find(element => element.innerText && element.innerText.includes(courseName));" +
                            "if (!row) { return false; }" +
                            "const edit = Array.from(row.querySelectorAll('a, button, i')).find(element => {" +
                            "  const text = (element.innerText || element.textContent || '').trim();" +
                            "  const title = element.getAttribute('title') || element.getAttribute('data-original-title') || '';" +
                            "  const href = element.getAttribute('href') || '';" +
                            "  const cls = element.getAttribute('class') || '';" +
                            "  return visible(element) && (" +
                            "    text.includes('Edit') || title.includes('Edit') || href.includes('Edit') ||" +
                            "    cls.includes('edit') || cls.includes('pencil')" +
                            "  );" +
                            "});" +
                            "if (!edit) { return false; }" +
                            "const clickable = edit.closest('a, button') || edit;" +
                            "clickable.scrollIntoView({block: 'center'});" +
                            "clickable.click();" +
                            "return true;",
                    courseName
            );
            return Boolean.TRUE.equals(clicked);
        });

        wait.until(ExpectedConditions.presenceOfElementLocated(finishButton));
    }

    private void clickCourseSearchButton() {
        wait.until(driver -> {
            Object clicked = ((JavascriptExecutor) driver).executeScript(
                    "const searchInput = arguments[0];" +
                            "const inputRect = searchInput.getBoundingClientRect();" +
                            "const visible = element => {" +
                            "  const rect = element.getBoundingClientRect();" +
                            "  const style = window.getComputedStyle(element);" +
                            "  return rect.width > 0 && rect.height > 0 && style.display !== 'none' && style.visibility !== 'hidden';" +
                            "};" +
                            "const button = Array.from(document.querySelectorAll('a, button')).find(element => {" +
                            "  const rect = element.getBoundingClientRect();" +
                            "  const cls = element.getAttribute('class') || '';" +
                            "  const text = (element.innerText || element.textContent || '').trim();" +
                            "  const hasSearchIcon = !!element.querySelector('[class*=search], [class*=fa-search]') || cls.includes('search');" +
                            "  return visible(element) && rect.top > inputRect.bottom && rect.top < inputRect.bottom + 140 &&" +
                            "    rect.left < inputRect.left + 120 && (hasSearchIcon || text === 'Search');" +
                            "});" +
                            "if (!button) { return false; }" +
                            "button.click();" +
                            "return true;",
                    driver.findElement(courseSearchField)
            );
            return Boolean.TRUE.equals(clicked);
        });
    }

    private void openCourseSearchFilter() {
        if (isCourseSearchPanelOpen()) {
            return;
        }

        wait.until(driver -> {
            Object clicked = ((JavascriptExecutor) driver).executeScript(
                    "const buttons = Array.from(document.querySelectorAll('a, button'));" +
                            "const visible = element => {" +
                            "  const rect = element.getBoundingClientRect();" +
                            "  const style = window.getComputedStyle(element);" +
                            "  return rect.width > 0 && rect.height > 0 && style.visibility !== 'hidden' && style.display !== 'none';" +
                            "};" +
                            "const searchButton = buttons.find(element => " +
                            "  visible(element) && (" +
                            "    (element.getAttribute('href') || '').includes('#search') ||" +
                            "    (element.getAttribute('data-target') || '').includes('search') ||" +
                            "    (element.getAttribute('data-bs-target') || '').includes('search')" +
                            "  )" +
                            ");" +
                            "if (!searchButton) { return false; }" +
                            "searchButton.scrollIntoView({block: 'center'});" +
                            "searchButton.click();" +
                            "return true;"
            );
            return Boolean.TRUE.equals(clicked);
        });

        wait.until(ExpectedConditions.visibilityOfElementLocated(courseSearchField));
    }

    private boolean isCourseSearchPanelOpen() {
        try {
            return driver.findElement(courseSearchField).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private By courseNameLocator(String courseName) {
        return By.xpath("//*[contains(normalize-space(), " + xpathText(courseName) + ")]");
    }

    private By courseRowLocator(String courseName) {
        return By.xpath("//tr[.//*[contains(normalize-space(), " + xpathText(courseName) + ")]]");
    }

    private By editCourseLocator(String courseName) {
        String course = xpathText(courseName);
        return By.xpath(
                "//tr[.//*[contains(normalize-space(), " + course + ")]]" +
                        "//a[contains(normalize-space(),'Edit') or contains(@href,'Edit') or contains(@title,'Edit') or .//*[contains(@class,'edit')]]" +
                        " | //tr[.//*[contains(normalize-space(), " + course + ")]]" +
                        "//button[contains(normalize-space(),'Edit') or contains(@title,'Edit') or .//*[contains(@class,'edit')]]"
        );
    }

    private String xpathText(String text) {
        if (!text.contains("'")) {
            return "'" + text + "'";
        }

        String[] parts = text.split("'");
        StringBuilder xpath = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                xpath.append(", \"'\", ");
            }
            xpath.append("'").append(parts[i]).append("'");
        }
        xpath.append(")");
        return xpath.toString();
    }
}
