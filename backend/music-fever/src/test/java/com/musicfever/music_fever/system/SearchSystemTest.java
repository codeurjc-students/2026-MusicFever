package com.musicfever.music_fever.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class SearchSystemTest {
    private static final String BASE_URL = "http://localhost:4200";
    private static final String SEARCH = "Taylor Swift";

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(120));
    }

    @AfterEach
    void closeTest() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Tag("system")
    void searchAndNavigateThroughResults(){
        driver.get(BASE_URL + "/home");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-input")));

        // Search for something
        driver.findElement(By.id("search-input")).sendKeys(SEARCH);
        driver.findElement(By.id("submit-search")).click();

        // Check if there is a result with the artist searched
        WebElement correctCard = getSearchCardForArtist();

        if (correctCard == null){
            driver.findElement(By.id("forceExternal")).click();
            correctCard = getSearchCardForArtist();
        }

        if (correctCard == null){
            fail("There is no card with artist name: " + SEARCH);
        }

        // Navigate to the artist page
        correctCard.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")));
        assertEquals(SEARCH, driver.findElement(By.tagName("h3")).getText());

        // Check the album tab
        List<WebElement> albumCards = driver.findElements(By.tagName("app-album-card"));
        assertFalse(albumCards.isEmpty(), "No album found");

        List<WebElement> paginators = driver.findElements(By.tagName("mat-paginator"));
        assertFalse(paginators.isEmpty(), "No paginator found");

        WebElement nextButton = driver.findElement(By.cssSelector(
            "mat-paginator button.mat-mdc-paginator-navigation-next"
        ));

        List<String> firstPage = driver.findElements(By.tagName("app-album-card"))
            .stream()
            .map(WebElement::getText)
            .toList();

        if (nextButton.isEnabled() && !"true".equalsIgnoreCase(nextButton.getDomAttribute("aria-disabled"))) {
            nextButton.click();

            new WebDriverWait(driver, Duration.ofSeconds(10))
                .ignoring(StaleElementReferenceException.class)
                .until(webDriver -> {
                    List<String> secondPage = webDriver.findElements(By.tagName("app-album-card"))
                        .stream().map(WebElement::getText).toList();

                    return !secondPage.isEmpty() && !secondPage.equals(firstPage);
                });
        }

        // Check the tracks tab
        WebElement popularTracksTab = driver.findElement(
            By.xpath("//div[@role='tab'][.//span[normalize-space()='popular tracks']]")
        );

        popularTracksTab.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-track-card")));
        List<WebElement> trackCards = driver.findElements(By.tagName("app-track-card"));
        assertFalse(trackCards.isEmpty(), "No tracks found");

        // Go to album page
        WebElement albumsTab = driver.findElement(
            By.xpath("//div[@role='tab'][.//span[normalize-space()='albums']]")
        );

        albumsTab.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-album-card")));
        WebElement firstAlbumCard = driver.findElement(By.tagName("app-album-card"));
        firstAlbumCard.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")));

        WebElement artistLink = driver.findElement(By.cssSelector("p.description a"));
        assertEquals(SEARCH, artistLink.getText());

        trackCards = driver.findElements(By.cssSelector("#tracklist app-track-card"));
        assertFalse(trackCards.isEmpty(), "No tracklist found for the album");

        // Back to the artist page
        artistLink.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h3")));
        assertEquals(SEARCH, driver.findElement(By.tagName("h3")).getText());
    }

    private WebElement getSearchCardForArtist() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("results")));
        List<WebElement> artistCards = driver.findElements(By.cssSelector(".scroll-content app-artist-card"));

        WebElement correctCard = null;
        for (WebElement card : artistCards) {
            boolean matches = card.getText().lines()
                .map(String::strip)
                .anyMatch(SEARCH::equalsIgnoreCase);

            if (matches) {
                correctCard = card;
            }
        }
        return correctCard;
    }
}