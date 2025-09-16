package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;

public class DynamicLoadingTraceTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @Test
    void testDynamicLoadingWithTrace() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();


        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");
        Assertions.assertEquals("Dynamically Loaded Page Elements", page.locator("h3").textContent(),
                "Заголовок страницы не совпадает");
        page.click("button");
        page.locator("#finish").waitFor();

        Assertions.assertTrue(page.locator("#finish").isVisible(),
                "Элемент с текстом не отобразился");
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace-dynamic-loading.zip")));
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}