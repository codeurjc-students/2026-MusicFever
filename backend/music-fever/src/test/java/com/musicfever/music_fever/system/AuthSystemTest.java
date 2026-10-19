package com.musicfever.music_fever.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthSystemTest {
    private static final String BASE_URL = "http://localhost:4200";

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

    private void login(String userIdentification, boolean usernameMode) {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title")));

        // Change to log in with username or stay at log in with email
        WebElement identificationInput;
        if (usernameMode){
            driver.findElement(By.id("mode-link")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-input")));
            identificationInput = driver.findElement(By.id("username-input"));
        } else {
            identificationInput = driver.findElement(By.id("email-input"));
        }

        // Add credentials
        identificationInput.sendKeys(userIdentification);
        driver.findElement(By.id("password-input")).sendKeys("eras1325");

        // Log in
        driver.findElement(By.id("login-button")).click();
    }

    @ParameterizedTest(name = "{0}")
    @Tag("system")
    @CsvSource({
            "Login with email and logout, armin@gmail.com, false",
            "Login with username and logout, armiiin, true"
    })
    void loginUser(String testName, String userIdentification, boolean usernameMode){
        login(userIdentification, usernameMode);

        // Check if log in was successful
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        assertThat(driver.getCurrentUrl()).isEqualTo(BASE_URL + "/home");

        WebElement username = driver.findElement(By.id("user-layout")).findElement(By.id("username"));
        assertThat(username.getText()).contains("armiiin");
    }

    @Test
    @Tag("system")
    void logoutUser(){
        login("armiiin", true);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        assertThat(driver.getCurrentUrl()).isEqualTo(BASE_URL + "/home");

        // Logout
        driver.findElement(By.id("logout-link")).click();

        // Check if log out was successful
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        WebElement title = driver.findElement(By.tagName("h1"));
        assertThat(title.getText()).contains("to Music Fever");
    }

    @Test
    @Tag("system")
    void registerUser(){
        driver.get(BASE_URL + "/register");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-input")));

        // Add credentials
        driver.findElement(By.id("username-input")).sendKeys("test");
        driver.findElement(By.id("email-input")).sendKeys("test@gmail.com");

        List<WebElement> passwordInputs = driver.findElements(By.id("password-input"));
        for (WebElement passwordInput : passwordInputs) {
            passwordInput.sendKeys("testPassword");
        }

        // Register
        driver.findElement(By.id("register-button")).click();

        // Check if registration was successful
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-layout")));
        assertThat(driver.getCurrentUrl()).isEqualTo(BASE_URL + "/home");

        WebElement title = driver.findElement(By.tagName("h1"));
        assertThat(title.getText()).contains("test");
    }

    @Test
    @Tag("system")
    void toggleBetweenLoginAndRegister(){
        driver.get(BASE_URL + "/login");

        // Go to register
        wait.until(ExpectedConditions.elementToBeClickable(By.id("toggle-link"))).click();

        // Check out if it is register
        assertThat(driver.getCurrentUrl()).isEqualTo(BASE_URL + "/register");
        WebElement subtitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("subtitle"))
        );

        assertThat(subtitle.getText().toLowerCase()).contains("register");

        // Go to log in
        wait.until(ExpectedConditions.elementToBeClickable(By.id("toggle-link"))).click();

        // Check out if it is log in
        assertThat(driver.getCurrentUrl()).isEqualTo(BASE_URL + "/login");
        subtitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("subtitle"))
        );

        assertThat(subtitle.getText().toLowerCase()).contains("sign in");
    }
}
