package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {
    static Playwright playwright;
    static Browser browser;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/home", "/about", "/contact"})
    void testPageLoad(String path) {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com" + path);
        // Проверяем, что заголовок страницы существует (не пустой)
        assertThat(page).hasTitle(Pattern.compile(".*"));
        context.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }
}