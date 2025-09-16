package courseplayw;


import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Тесты для the-internet.herokuapp.com")
@Feature("Работа с JavaScript-алертами")
public class AdvancedReportingTest {

    private static ExtentReports extent;
    private Playwright playwright;
    private Browser browser;
    private Page page;
    private ExtentTest test;

    @BeforeAll
    static void setupExtent() {
        ExtentSparkReporter reporter = new ExtentSparkReporter("allure-results/extent-report.html");
        reporter.config().setDocumentTitle("Playwright Extent Report");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        test = extent.createTest(testInfo.getDisplayName());
        logExtent(Status.INFO, "Тест начат: " + testInfo.getDisplayName());
    }

    @Test
    @Story("Проверка JS Alert")
    @DisplayName("Тест взаимодействия с JS Alert")
    @Severity(SeverityLevel.NORMAL)
    @Description("Тест взаимодействия с JS Alert и проверка результата")
    void testJavaScriptAlert() throws IOException {
        try {
            navigateToAlertsPage();
            String alertMessage = handleJsAlert();
            verifyResultText();
            captureSuccessScreenshot();

            test.pass("Тест успешно завершен с сообщением: " + alertMessage);
            logExtent(Status.PASS, "Тест успешно завершен с сообщением: " + alertMessage);

        } catch (Exception e) {
            handleTestFailure(e);
            throw e;
        }
    }

    @Step("Открыть страницу с алертами")
    private void navigateToAlertsPage() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        assertEquals("JavaScript Alerts", page.locator("h3").textContent(),
                "Страница должна содержать заголовок 'JavaScript Alerts'");
        logExtent(Status.INFO, "Страница с алертами загружена");
    }

    @Step("Обработать JS Alert")
    private String handleJsAlert() {
        CompletableFuture<String> alertMessageFuture = new CompletableFuture<>();

        page.onDialog(dialog -> {
            String message = dialog.message();
            alertMessageFuture.complete(message);
            dialog.accept();
        });

        page.click("button[onclick='jsAlert()']");
        logExtent(Status.INFO, "Клик по кнопке JS Alert выполнен");

        String alertMessage = alertMessageFuture.getNow(null);
        assertNotNull(alertMessage, "Сообщение alert не было получено");
        return alertMessage;
    }

    @Step("Проверить текст результата")
    private void verifyResultText() {
        page.waitForSelector("#result", new Page.WaitForSelectorOptions().setTimeout(5000));
        String resultText = page.locator("#result").textContent();
        assertEquals("You successfully clicked an alert", resultText,
                "Текст результата должен соответствовать ожидаемому");
        logExtent(Status.INFO, "Результирующий текст проверен: " + resultText);
    }

    @Step("Сделать скриншот успешного выполнения")
    private void captureSuccessScreenshot() throws IOException {
        byte[] screenshot = page.screenshot();
        Path path = Paths.get("allure-results/success-screenshot.png");
        Files.write(path, screenshot);

        // Allure
        try (InputStream is = new ByteArrayInputStream(screenshot)) {
            Allure.addAttachment("Успешное выполнение", "image/png", is, ".png");
        }

        // ExtentReports
        test.pass("Скриншот успешного выполнения",
                MediaEntityBuilder.createScreenCaptureFromPath(path.toString()).build());
    }

    private void handleTestFailure(Exception e) {
        byte[] failureScreenshot = page.screenshot();
        Path path = Paths.get("allure-results/error-screenshot.png");
        try {
            Files.write(path, failureScreenshot);

            // Allure
            try (InputStream is = new ByteArrayInputStream(failureScreenshot)) {
                Allure.addAttachment("Ошибка теста", "image/png", is, ".png");
            }

            // ExtentReports
            test.fail("Тест упал: " + e.getMessage(),
                    MediaEntityBuilder.createScreenCaptureFromPath(path.toString()).build());
        } catch (IOException ex) {
            test.warning("Не удалось сохранить скриншот ошибки: " + ex.getMessage());
        }

        test.fail(e);
    }

    private void logExtent(Status status, String message) {
        test.log(status, message);
        System.out.println("[" + status + "] " + message); // Вывод в консоль
    }

    @AfterEach
    void tearDownEach() {
        page.close();
        browser.close();
        playwright.close();
    }

    @AfterAll
    static void tearDown() {
        extent.flush();
    }
}