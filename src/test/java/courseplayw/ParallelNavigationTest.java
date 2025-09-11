package courseplayw;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {

    private static Playwright playwright;
    private static Browser browserChrome;
    private static Browser browserFirefox;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browserChrome = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false));
        browserFirefox = playwright.firefox().launch(
                new BrowserType.LaunchOptions().setHeadless(false));
    }

    static Stream<Arguments> providePagesAndBrowsers() {
        String baseUrl = "https://the-internet.herokuapp.com";
        String[] paths = {"/", "/login", "/dropdown", "/javascript_alerts", "/checkboxes", "/hover", "/status_codes"};
        return Stream.of(
                Arguments.of(browserChrome, baseUrl, paths),
                Arguments.of(browserFirefox, baseUrl, paths)
        );
    }

    @ParameterizedTest
    @MethodSource("providePagesAndBrowsers")
    void testPageLoad(Browser browser, String baseUrl, String[] paths) {
        for (String path : paths) {
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate(baseUrl + path);

            // Проверяем, что заголовок страницы не пустой
            assertThat(page).hasTitle(Pattern.compile(".*"));

            context.close();
        }
    }

    @AfterAll
    static void tearDown() {
        browserChrome.close();
        browserFirefox.close();
        playwright.close();
    }
}