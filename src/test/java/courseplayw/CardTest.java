package courseplayw;

import base.BaseTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class CardTest extends BaseTest {
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setup() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/")));
        page = context.newPage();
    }

    @Test
    void testCartActions() {
        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");

        // Добавление товара
        page.click("#add-to-cart");
        page.locator("#cart").screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("cart_after_add.png")));

        // Удаление товара
        page.click("#remove-from-cart");
        page.locator("#cart").screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("cart_after_remove.png")));
    }

    private Path getTimestampPath(String filename) {
        return Paths.get(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + "/" + filename);
    }

    @Test
    void testHomePageVisual() throws IOException {
        page.navigate("https://the-internet.herokuapp.com");
        Path actual = Paths.get("actual.png");
        page.screenshot(new Page.ScreenshotOptions().setPath(actual));

        long mismatch = Files.mismatch(actual, Paths.get("expected.png"));
        assertThat(mismatch).isEqualTo(-1); // -1 = файлы идентичны
    }

    @AfterEach
    void attachScreenshotOnFailure(ExtensionContext extensionContext) {
        // Проверяем наличие исключения
        if (extensionContext.getExecutionException().isPresent()) {
            byte[] screenshot = page.screenshot();
            Allure.addAttachment(
                    "Screenshot on Failure",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
        }
        context.close();
    }
}
