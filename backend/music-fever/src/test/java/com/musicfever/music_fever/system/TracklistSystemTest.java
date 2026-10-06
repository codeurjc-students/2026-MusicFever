package com.musicfever.music_fever.system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicfever.music_fever.model.Track;

import static org.assertj.core.api.Assertions.assertThat;

public class TracklistSystemTest {
    private static final String BASE_URL = "http://localhost:4200";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpTest() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--incognito");
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

    private HttpClient createInsecureHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Track> getTracklistFromAPI(){
        HttpClient client = createInsecureHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://localhost:8443/api/v1/tracks/"))
            .GET()
            .build();

        try {
            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(
                response.body(),
                new TypeReference<List<Track>>() {}
            );

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Could not retrieve tracks from API", e);
        }
    }

    @Test
    @Tag("system")
    void checkTracklistOnMainPage() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-track")));
        List<WebElement> tracks = driver.findElements(By.tagName("app-track"));
        List<Track> expectedTracks = this.getTracklistFromAPI();

        assertEquals( "TrackList", title.getText());
        assertEquals(expectedTracks.size(), tracks.size());

        for (int i = 0; i < expectedTracks.size(); i++){
            assertThat(tracks.get(i).getText()).contains(expectedTracks.get(i).getName());
        }
    }

}
