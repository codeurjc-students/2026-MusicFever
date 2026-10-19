package com.musicfever.music_fever.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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

@Tag("system")
public class LayoutSystemTest {

    private static final String BASE_URL = "http://localhost:4200";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUpTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.setAcceptInsecureCerts(true);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        driver.get(BASE_URL);
    }

    @AfterEach
    void closeTest() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void navigateTo(String section) {
        click(By.cssSelector("#" + section + " a"));
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/" + section));
        assertThat(driver.findElements(By.cssSelector("#" + section + " a.active"))).isNotEmpty();
    }

    private void assertInactive(String section) {
        assertThat(driver.findElements(By.cssSelector("#" + section + " a.active"))).isEmpty();
    }

    @Test
    @Tag("system")
    @DisplayName("Should navigate to home by default")
    void homeNavigationTest() {
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/home"));

        assertThat(driver.findElements(
            By.cssSelector("#home a.active")
        )).isNotEmpty();
    }

    @Test
    @Tag("system")
    @DisplayName("Should navigate to login and register")
    void authNavigationTest() {
        click(By.cssSelector("#user-layout a:first-of-type"));
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/login"));
        driver.get(BASE_URL);

        click(By.cssSelector("#user-layout a:last-of-type"));
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/register"));
    }

    @Test
    @Tag("system")
    @DisplayName("Should navigate between main sections")
    void mainNavigationTest() {
        // Search
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-input"))).sendKeys("Test");
        click(By.id("submit-search"));
        wait.until(ExpectedConditions.urlContains("/search"));

        assertThat(driver.findElements(By.id("search"))).isNotEmpty();
        assertThat(driver.findElements(By.cssSelector("#search a.active"))).isNotEmpty();
        assertInactive("home");

        // Player
        navigateTo("player");
        assertThat(driver.findElements(By.id("search"))).isEmpty();

        // Rooms
        navigateTo("rooms");
        assertInactive("player");

        // Stats
        navigateTo("stats");
        assertInactive("rooms");

        // Home
        navigateTo("home");
        assertInactive("stats");
    }

    @Test
    @Tag("system")
    @DisplayName("Should navigate through footer internal links")
    void footerNavigationTest() {
        String[] sections = {"faq", "privacy", "terms"};

        for (String section : sections) {
            driver.get(BASE_URL);
            click(By.cssSelector("#center-layout a[href='/" + section + "']"));
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/" + section));
        }
    }

    @Test
    @Tag("system")
    @DisplayName("Should have correct external links")
    void externalLinksTest() {
        driver.get(BASE_URL);

        List<WebElement> links = driver.findElements(By.cssSelector("#center-layout a"));
        links.get(0).click();
        assertThat(driver.getCurrentUrl()).contains("medium");
        driver.get(BASE_URL);

        links = driver.findElements(By.cssSelector("#app-icons a"));
        links.get(0).click();
        assertThat(driver.getCurrentUrl()).contains("github");
        driver.get(BASE_URL);

        links = driver.findElements(By.cssSelector("#app-icons a"));
        links.get(1).click();
        assertThat(driver.getCurrentUrl()).contains("instagram");
    }
}