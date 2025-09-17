package courseplayw;

import com.microsoft.playwright.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoApiTest {
    Playwright playwright;
    APIRequestContext requestContext;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://jsonplaceholder.typicode.com")
        );
    }

    @Test
    void testTodoApi() throws IOException {
        // 1. Выполняем GET-запрос
        APIResponse response = requestContext.get("/todos/1");

        // 2. Проверяем статус-код
        assertEquals(200, response.status(), "Ожидается статус-код 200");

        // 3. Парсим JSON-ответ
        String responseBody = response.text();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // 4. Проверяем структуру JSON
        Assertions.assertTrue(jsonNode.has("userId"), "Поле userId отсутствует");
        Assertions.assertTrue(jsonNode.has("id"), "Поле id отсутствует");
        Assertions.assertTrue(jsonNode.has("title"), "Поле title отсутствует");
        Assertions.assertTrue(jsonNode.has("completed"), "Поле completed отсутствует");

        // Выводим результат для проверки
        System.out.println("Полученный JSON: " + jsonNode.toPrettyString());
    }

    @AfterEach
    void tearDown() {
        requestContext.dispose();
        playwright.close();
    }
}