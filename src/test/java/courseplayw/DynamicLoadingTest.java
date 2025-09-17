package courseplayw;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DynamicLoadingTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @Test
    void testDynamicLoading() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();

        // Включаем трассировку
        context.tracing().start(new Tracing.StartOptions());

        page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

        // Перехватываем сетевые запросы
        page.onResponse(response -> {
            if (response.url().contains("/dynamic_loading")) {
                Assertions.assertEquals(200, response.status(), "Ожидается успешный статус-код 200");
                System.out.println("Запрос завершён успешно: " + response.url());
            }
        });

        // Запускаем процесс загрузки
        page.click("button");

        // Ожидаем появления текста "Hello World!"
        Locator finishText = page.locator("#finish");
        assertThat(finishText).hasText("Hello World!", new LocatorAssertions.HasTextOptions().setTimeout(15000));

        // Останавливаем трассировку и сохраняем в файл
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace/trace-success.zip")));
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}