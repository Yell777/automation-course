package courseplayw;

import com.github.javafaker.Faker;
import com.microsoft.playwright.*;

public class FakerGenerTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();

            // Генерация данных
            Faker faker = new Faker();
            String mockName = faker.name().fullName();

            // Мокирование API
            page.route("**/dynamic_content", route -> {
                route.fulfill(new Route.FulfillOptions()
                        .setBody("<div class='large-10 columns'>" + mockName + "</div>"));
            });

            // Запуск теста
            page.navigate("https://the-internet.herokuapp.com/dynamic_content");
            Locator content = page.locator(".large-10.columns");
            assert content.textContent().contains(mockName);
        }
    }
}