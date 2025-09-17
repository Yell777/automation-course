package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HoverTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(); // Можно использовать launch(new BrowserType.LaunchOptions().setHeadless(false))
    }

    @BeforeEach
    void setupTest() {
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testHoverProfiles() {
        page.navigate("https://the-internet.herokuapp.com/hovers");

        Locator figures = page.locator(".figure");
        int count = figures.count();

        for (int i = 0; i < count; i++) {
            Locator figure = figures.nth(i);

            // Наводим курсор
            figure.hover();

            // Ожидаем появления ссылки "View profile"
            Locator profileLink = figure.locator("text=View profile");
            profileLink.waitFor();
            assertTrue(profileLink.isVisible());

            // Кликаем по ссылке
            profileLink.click();

            // Проверяем, что URL содержит /users/{id}
            String url = page.url();
            assertTrue(url.matches(".*/users/\\d+"), "URL должен содержать /users/{id}, но был: " + url);

            // Возвращаемся назад
            page.goBack();

            // Ждём, чтобы элементы снова загрузились
            page.waitForTimeout(500);
        }
    }

    @AfterEach
    void teardownTest() {
        context.close();
        page.close();
    }

    @AfterAll
    static void teardownClass() {
        browser.close();
        playwright.close();
    }
}