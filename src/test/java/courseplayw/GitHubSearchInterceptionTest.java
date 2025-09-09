package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitHubSearchInterceptionTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        //Код...
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();

        // Перехват запроса поиска
        context.route("**/search**", route -> {
            // Получаем оригинальный URL
            String originalUrl = route.request().url();

            // Декодируем и модифицируем параметры
            String modifiedUrl = originalUrl.contains("q=")
                    ? originalUrl.replaceAll("q=[^&]+", "q=stars%3A%3E10000")
                    : originalUrl + (originalUrl.contains("?") ? "&" : "?") + "q=stars%3A%3E10000";

            // Продолжаем запрос с модифицированным URL
            route.resume(new Route.ResumeOptions().setUrl(modifiedUrl));
        });
    }

    @Test
    void testSearchModification() {
        page.navigate("https://github.com/search?q=java");

        // Ожидаем появления результатов
        page.locator("//a[contains(normalize-space(), 'freeCodeCamp/freeCodeCamp')]").first().waitFor();

        // Проверяем модифицированный запрос в UI
        String searchValue = page.locator("input#query-builder-test[type='text']").inputValue();
        assertEquals("stars:>10000", searchValue);
    }

    @AfterEach
    void tearDown() {
        playwright.close();
    }
}
