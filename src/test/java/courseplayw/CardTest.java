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
    private Path actualScreenshot;


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
        page.locator("button[onclick]").filter().click();
        page.locator("//button[contains(normalize-space(), 'Add Element')]").screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("cart_after_add.png")));
        // Удаление товара
        page.locator("//button[contains(normalize-space(), 'Delete')]").click();
        page.locator("//button[contains(normalize-space(), 'Add Element')]").screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("cart_after_remove.png")));
    }

    private Path getTimestampPath(String filename) {
        return Paths.get(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + "/" + filename);
    }


    @AfterEach
    void attachScreenshotOnFailure() throws IOException {

        if (actualScreenshot != null && Files.exists(actualScreenshot)) {
            Files.delete(actualScreenshot);
        }

        // Закрываем контекст Playwright
        if (context != null) {
            context.close();
        }
    }
}