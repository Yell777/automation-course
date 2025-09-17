package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MobileDragAndDropTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // Ручная настройка параметров Samsung Galaxy S22 Ultra
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Linux; Android 12; SM-S908B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Mobile Safari/537.36")
                .setViewportSize(384, 873)  // Разрешение экрана
                .setDeviceScaleFactor(3.5)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testDragAndDropMobile() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");

        Locator columnA = page.locator("#column-a");
        Locator columnB = page.locator("#column-b");

        // 1. Проверка начального состояния
        assertEquals("A", columnA.textContent(), "Исходный текст в зоне A должен быть 'A'");
        assertEquals("B", columnB.textContent(), "Исходный текст в зоне B должен быть 'B'");

        // 2. Перетаскивание через JS (так как на мобильных устройствах нативный drag & drop может не работать)
        page.evaluate("() => {\n" +
                "  const dataTransfer = new DataTransfer();\n" +
                "  const dragStartEvent = new DragEvent('dragstart', { dataTransfer });\n" +
                "  const dropEvent = new DragEvent('drop', { dataTransfer, bubbles: true });\n" +
                "\n" +
                "  // Имитация начала перетаскивания\n" +
                "  document.querySelector('#column-a').dispatchEvent(dragStartEvent);\n" +
                "\n" +
                "  // Имитация drop в зоне B\n" +
                "  document.querySelector('#column-b').dispatchEvent(dropEvent);\n" +
                "}");

        // 3. Проверка, что текст в зоне B изменился на "A"
        page.waitForTimeout(1000); // небольшая пауза для обновления DOM
        assertEquals("A", columnB.textContent(), "После перетаскивания текст в зоне B должен быть 'A'");
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}