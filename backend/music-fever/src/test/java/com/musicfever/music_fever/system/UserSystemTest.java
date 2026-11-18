package com.musicfever.music_fever.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UserSystemTest {
    private static final String BASE_URL = "http://localhost:4200";
    private static final String[] USERNAME = {"testUser", "editableUser"};
    private static final String PASSWORD = "passTest123";
    private static final String[] EMAIL = {"test@gmail.com", "editableEmail@gmail.com"};

    private static final String NEW_EMAIL = "newEmail@gmail.com";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpTest() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--headless=new");
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.setAcceptInsecureCerts(true);

        driver = new ChromeDriver(chromeOptions);
        driver.get(BASE_URL);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @AfterEach
    void closeTest() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void register(int mode){
        driver.get(BASE_URL + "/home");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auth-link")));

        WebElement registerLink = driver.findElements(By.id("auth-link")).get(1); // register link
        registerLink.click();

        // On register page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("register-form")));

        driver.findElement(By.id("username-input")).sendKeys(USERNAME[mode]);
        driver.findElement(By.id("email-input")).sendKeys(EMAIL[mode]);

        List<WebElement> passwordInputs = driver.findElements(By.id("password-input"));
        for (WebElement passwordInput : passwordInputs) {
            passwordInput.sendKeys(PASSWORD);
        }

        // Register
        driver.findElement(By.id("register-button")).click();
    }

    private void login(int mode){
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title")));

        // Change to log in with username
        driver.findElement(By.id("mode-link")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-input")));
        WebElement identificationInput = driver.findElement(By.id("username-input"));

        // Add credentials
        identificationInput.sendKeys(USERNAME[mode]);
        driver.findElement(By.id("password-input")).sendKeys(PASSWORD);

        // Log in
        driver.findElement(By.id("login-button")).click();
    }

    private void authenticate(int mode){
        register(mode);
        if (!driver.getCurrentUrl().endsWith("/home")){
            login(mode);
        }
    }

    private void goToProfile(int mode){
        authenticate(mode);

        // Go to Profile Page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-info")));
        driver.findElement(By.id("user-info")).click();
    }

    @Test
    @Tag("system")
    void searchAndNavigateThroughResults(){
        goToProfile(0);
        assertTrue(driver.getCurrentUrl().endsWith("/profile"));

        // Check if data is well display on the profile
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h4")));
        WebElement title = driver.findElement(By.tagName("h4"));
        assertEquals("Your Profile", title.getText());

        List<WebElement> infoBits = driver.findElements(By.className("info-dt"));

        String usernameDisplay = infoBits.get(0).findElement(By.tagName("dd")).getText();
        String emailDisplay = infoBits.get(1).findElement(By.tagName("dd")).getText();
        String dateDisplay = infoBits.get(2).findElement(By.tagName("dd")).getText();
        String pictureUrl = driver.findElement(By.id("profile-picture")).getAttribute("src");

        assertEquals(USERNAME[0], usernameDisplay);
        assertEquals(EMAIL[0], emailDisplay);
        assertEquals(LocalDate.now().toString(), dateDisplay);
        assertTrue(pictureUrl.contains("image_not_found.png"));

        WebElement appConnected = driver.findElement(By.className("app-container"));
        assertEquals("No music account connected", appConnected.findElement(By.tagName("span")).getText());
    }

    @Test 
    @Tag("system")
    void profileNavigationTest(){
        goToProfile(0);

        By profileTab = By.xpath(
            "//mat-tab-group[@class and contains(concat(' ', normalize-space(@class), ' '), ' profile-tabs ')]"
                + "/mat-tab-header//*[@role='tab'][.//span[normalize-space()='Profile']]"
        );
        By myRoomsTab = By.xpath(
            "//mat-tab-group[@class and contains(concat(' ', normalize-space(@class), ' '), ' profile-tabs ')]"
                + "/mat-tab-header//*[@role='tab'][.//span[normalize-space()='My Rooms']]"
        );

        wait.until(ExpectedConditions.elementToBeClickable(myRoomsTab)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".profile-room-filter")
        ));
        assertEquals("true", driver.findElement(myRoomsTab).getDomAttribute("aria-selected"));

        wait.until(ExpectedConditions.elementToBeClickable(profileTab)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".profile-container")
        ));
        assertEquals("true", driver.findElement(profileTab).getDomAttribute("aria-selected"));

        WebElement analyticsButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[normalize-space()='Go to Analytics']]")
            )
        );

        analyticsButton.click();

        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/stats"));
        assertTrue(driver.getCurrentUrl().endsWith("/stats"));
    }

    @Test 
    @Tag("system")
    void editProfileTest_wellDisplayed(){
        goToProfile(0);

        WebElement editProfileButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[normalize-space()='Edit Profile']]")
            )
        );

        editProfileButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h4")));
        WebElement title = driver.findElement(By.tagName("h4"));
        assertEquals("Edit Profile", title.getText());

        WebElement emailInput = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='email'][aria-label='Email']")
            )
        );

        assertEquals(EMAIL[0], emailInput.getDomProperty("value"));

        WebElement spotifyOption = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("clickable"))
        );
        assertEquals("Spotify", spotifyOption.getText());
    }

    @Test 
    @Tag("system")
    void editProfileTest_updateEmail(){
        goToProfile(1);

        WebElement editProfileButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[normalize-space()='Edit Profile']]")
            )
        );

        editProfileButton.click();

        WebElement emailInput = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='email'][aria-label='Email']")
            )
        );
        emailInput.clear();
        emailInput.sendKeys(NEW_EMAIL);

        WebElement saveChangesButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector("button.save-btn"))
        );

        saveChangesButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("info-dt")));
        List<WebElement> infoBits = driver.findElements(By.className("info-dt"));

        String emailDisplay = infoBits.get(1).findElement(By.tagName("dd")).getText();
        assertEquals(NEW_EMAIL, emailDisplay);
    }

    @Test 
    @Tag("system")
    void editProfileTest_updatePicture() throws IOException{
        goToProfile(1);

        WebElement editProfileButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[normalize-space()='Edit Profile']]")
            )
        );

        editProfileButton.click();

        WebElement photoInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".profile-info input[type='file']")
            )
        );

        Path photoPath = Path.of("src/test/resources/profile-test.jpg").toAbsolutePath();
        photoInput.sendKeys(photoPath.toString());
        
        String expectedSrc = "data:image/jpeg;base64,"
            + Base64.getEncoder().encodeToString(Files.readAllBytes(photoPath));

        wait.until(ExpectedConditions.attributeToBe(
            By.cssSelector(".content img#profile-picture"), "src", expectedSrc
        ));

        WebElement saveChangesButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector("button.save-btn"))
        );

        saveChangesButton.click();

        // Check image is changed in profile
        WebElement profilePicture = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".content img#profile-picture")
            )
        );

        assertEquals(expectedSrc, profilePicture.getDomAttribute("src"));

        // Check image is changed in side-header
        WebElement sideHeaderPicture = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("app-side-header img#profile-picture")
            )
        );

        assertEquals(expectedSrc, sideHeaderPicture.getDomAttribute("src"));
    }

    @Test 
    @Tag("system")
    void logoutTest(){
        goToProfile(0);

        WebElement logoutButton = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("button.logout-btn")
            )
        );

        logoutButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auth-link")));
        assertTrue(driver.getCurrentUrl().endsWith("/home"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title")));

        List<WebElement> authLinks = wait.until(
            ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("app-side-header #auth-links > a"), 2
            )
        );

        assertEquals("Log In", authLinks.get(0).getText());
    }
}