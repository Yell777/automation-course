package tests;

import com.microsoft.playwright.*;
import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.DragDropPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DragDropTest {

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

    @AfterEach
    @Step("Закрытие ресурсов")
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
    @Test
    public void testDragAndDrop() {
        DragDropPage dragDropPage = new DragDropPage(page);
        dragDropPage.navigateTo("https://the-internet.herokuapp.com/drag_and_drop")
                .dragDropArea()
                .dragAToB();
        assertEquals("A", dragDropPage.dragDropArea().getTextB());
    }
}
