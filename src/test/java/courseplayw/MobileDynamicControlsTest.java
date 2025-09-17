package courseplayw;

import  com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MobileDynamicControlsTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();

        // Настройка параметров iPad Pro 11
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/19A346 Safari/604.1")
                .setViewportSize(834, 1194)
                .setDeviceScaleFactor(2)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)); // Установите headless на false для визуализации
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testInputEnabling() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        // Кликаем на кнопку "Enable"
        page.click("button:text('Enable')");

        // Ждем, пока поле ввода станет активным
        Locator inputField = page.locator("input[type='text']");
        page.waitForFunction("() => !document.querySelector(\"input[type='text']\").hasAttribute('disabled')");

        // Проверяем, что поле ввода действительно активно
        assertTrue(inputField.isEnabled(), "Поле ввода должно быть активным");
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}