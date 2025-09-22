package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeApiUiTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // Настройка API контекста
        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://the-internet.herokuapp.com")
        );

        // Настройка браузера
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(500)
        );

        page = browser.newPage();

        // Переход на страницу статус-кодов
        page.navigate("https://the-internet.herokuapp.com/status_codes");
        page.waitForSelector("div.example");
    }

    @Test
    void testStatusCodesCombined() {
        int[] codes = {200, 404};

        for (int code : codes) {
            int apiStatus = getApiStatusCode(code);
            int uiStatus = getUiStatusCode(code);

            // Сравниваем статус-коды из API и UI
            assertEquals(apiStatus, uiStatus, "Статус-коды не совпадают для кода: " + code);
        }
    }

    private int getApiStatusCode(int code) {
        APIResponse response = apiRequest.get("/status_codes/" + code);
        return response.status();
    }

    private int getUiStatusCode(int code) {
        Locator link = page.locator("text=" + code).first();

        Response response = page.waitForResponse(
                res -> res.url().endsWith("/status_codes/" + code),
                () -> link.click(new Locator.ClickOptions().setTimeout(15000))
        );

        return response.status();
    }

    @AfterEach
    void teardown() {
            apiRequest.dispose();
            browser.close();
            playwright.close();
    }
}