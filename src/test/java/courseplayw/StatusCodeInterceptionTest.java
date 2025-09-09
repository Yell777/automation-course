package courseplayw;

import base.BaseTest;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeInterceptionTest extends BaseTest {

    @Test
    void testMockedStatusCode() {
        page.navigate("https://the-internet.herokuapp.com/status_codes");
        // Клик по ссылке "404"
        page.click("//a[contains(normalize-space(), '404')]");
        // Проверка, что отображается мокированный текст
        String responseText = page.textContent("h3");
        assertEquals("Mocked Success Response", responseText.trim());
    }

}

