package courseplayw;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;

import static java.awt.Color.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileUploadTest {
    private Playwright playwright;
    private APIRequestContext request;

    private byte[] generatedPngBytes;

    @BeforeEach
    void setUp() throws IOException {
        playwright = Playwright.create();
        request = playwright.request().newContext();

        // Генерация PNG в памяти
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(WHITE);
        g.fillRect(0, 0, 100, 100);
        g.setColor(RED);
        g.fillRect(25, 25, 50, 50);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        generatedPngBytes = baos.toByteArray();
    }

    @Test
    void testFileUploadAndDownload() throws IOException {
        // Загрузка файла через multipart/form-data
        Path testFile = Files.createTempFile("test-", ".png");
        try {
            Files.write(testFile, generatedPngBytes);

            APIResponse uploadResponse = request.post(
                    "https://httpbin.org/post",
                    RequestOptions.create().setMultipart(
                            FormData.create().set("file", testFile)
                    )
            );


            assertTrue(uploadResponse.ok(), "Upload request failed: " + uploadResponse.statusText());

            String responseBody = uploadResponse.text();
            assertTrue(responseBody.contains("data:image/png;base64"), "Base64 data not found in response");

            // Извлечение base64 данных
            String base64Data = responseBody.split("\"file\": \"")[1].split("\"")[0];
            String base64Content = base64Data.split(",")[1];
            byte[] receivedBytes = Base64.getDecoder().decode(base64Content);

            // Проверка целостности данных
            assertArrayEquals(generatedPngBytes, receivedBytes, "Uploaded and received file content do not match");

            // Скачивание эталонного PNG
            APIResponse downloadResponse = request.get("https://httpbin.org/image/png");
            assertTrue(downloadResponse.ok(), "Download request failed: " + downloadResponse.statusText());

            // Проверка сигнатуры PNG
            byte[] content = downloadResponse.body();
            assertArrayStartsWithPngSignature(content);
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    private void assertArrayStartsWithPngSignature(byte[] content) {
        assert content.length >= 8 : "Content too short for PNG signature";
        assertEquals(0x89, content[0] & 0xFF);
        assertEquals(0x50, content[1] & 0xFF); // 'P'
        assertEquals(0x4E, content[2] & 0xFF); // 'N'
        assertEquals(0x47, content[3] & 0xFF); // 'G'
        assertEquals(0x0D, content[4] & 0xFF);
        assertEquals(0x0A, content[5] & 0xFF);
        assertEquals(0x1A, content[6] & 0xFF);
        assertEquals(0x0A, content[7] & 0xFF);
    }

    @AfterEach
    void tearDown() {
        request.dispose();
        playwright.close();
    }
}