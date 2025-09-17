package courseplayw;

import com.microsoft.playwright.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;

public class DynamicControlsTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(); // Можно использовать setHeadless(false) для просмотра
        page = browser.newPage();
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
    }

    @Test
    void testDynamicCheckbox() {
        // Локаторы
        Locator checkbox = page.locator("input[type=checkbox]");
        Locator removeButton = page.locator("button", new Page.LocatorOptions().setHasText("Remove"));
        Locator message = page.locator("#message");

        // 1. Кликаем на "Remove"
        removeButton.click();

        // 2. Ожидаем исчезновения чекбокса
        checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));

        // 3. Проверяем появление сообщения "It's gone!"
        message.waitFor();
        assertTrue(message.isVisible());
        assertThat(message).hasText("It's gone!");

        // 4. Кликаем на "Add"
        page.locator("button", new Page.LocatorOptions().setHasText("Add")).click();

        // 5. Ожидаем появления чекбокса
        checkbox.waitFor();

        assertTrue(checkbox.isVisible());
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}