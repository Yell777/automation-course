package courseplayw;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;


import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Веб-интерфейс тестов")
@Feature("Операции с чекбоксами")
@DisplayName("Тесты для страницы /checkboxes")
public class CheckboxTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    @Step("Инициализация браузера и контекста")
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    @Story("Проверка работы чекбоксов")
    @DisplayName("Тестирование выбора и снятия чекбоксов")
    @Severity(SeverityLevel.CRITICAL)
    void testCheckboxes() {
        try {
            navigateToCheckboxesPage();
            verifyInitialState();
            toggleCheckboxes();
            verifyToggledState();
        } catch (Exception e) {
            // Делаем скриншот при ошибке и прикрепляем к Allure
            Allure.addAttachment("Скриншот при ошибке", "image/png",
                    new ByteArrayInputStream(page.screenshot()), ".png");
            throw e;
        }
    }

    @Step("Переход на страницу /checkboxes")
    private void navigateToCheckboxesPage() {
        page.navigate("https://the-internet.herokuapp.com/checkboxes");
    }

    @Step("Проверка начального состояния чекбоксов")
    private void verifyInitialState() {
        Locator checkbox1 = page.locator("input[type=checkbox]").nth(0);
        Locator checkbox2 = page.locator("input[type=checkbox]").nth(1);

        assertFalse(checkbox1.isChecked(), "Первый чекбокс должен быть неотмеченным");
        assertTrue(checkbox2.isChecked(), "Второй чекбокс должен быть отмеченным");
    }

    @Step("Изменение состояния чекбоксов")
    private void toggleCheckboxes() {
        page.check("input[type=checkbox]:nth-of-type(1)");
        page.uncheck("input[type=checkbox]:nth-of-type(2)");
    }

    @Step("Проверка изменённого состояния чекбоксов")
    private void verifyToggledState() {
        Locator checkbox1 = page.locator("input[type=checkbox]").nth(0);
        Locator checkbox2 = page.locator("input[type=checkbox]").nth(1);

        assertTrue(checkbox1.isChecked(), "Первый чекбокс должен быть отмеченным");
        assertFalse(checkbox2.isChecked(), "Второй чекбокс должен быть неотмеченным");
    }

    @AfterEach
    @Step("Закрытие ресурсов")
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}