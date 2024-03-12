package greencity.mapping;

import greencity.service.MultipartFileImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MultipartBase64ImageMapperTest {
    @InjectMocks
    MultipartBase64ImageMapper mapper;


    final String base64Image = "/9j/4AAQSkZJRgABAQEAYABgAAD/4QAiRXhpZgAATU0AKgAAAAgAAQ" +
            "ESAAMAAAABAAEAAAAAAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwc" +
            "ICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwM" +
            "DAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAABAAEDA" +
            "SIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAw" +
            "UFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJic" +
            "oKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWW" +
            "l5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09" +
            "fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBA" +
            "QAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJyg" +
            "pKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaX" +
            "mJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+" +
            "Pn6/9oADAMBAAIRAxEAPwD9/KKKKAP/2Q==";

    final byte[] imageBytes = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
        0, 0, 0, 1, 0, 0, 0, 1, 8, 2, 0, 0, 0, -112, 119, 83, -34, 0, 0, 0, 12, 73, 68,
        65, 84, 120, 94, 99, -8, -1, -1, 63, 0, 5, -2, 2, -2, -12, 42, -45, 9, 0, 0, 0,
        0, 73, 69, 78, 68, -82, 66, 96, -126};

    @Test
    public void test_convert_validBase64ImageToMultipartFile() throws IOException {

        String testBase64Image = "data:image/jpeg;base64," + base64Image;
        MultipartFile expectedMultipartFile =
                    new MultipartFileImpl("mainFile", "tempImage.jpg",
                            "image/jpeg", imageBytes);

        MultipartFile actualMultipartFile = mapper.convert(testBase64Image);

        assertEquals(expectedMultipartFile.getName(), actualMultipartFile.getName());
        assertEquals(expectedMultipartFile.getOriginalFilename(), actualMultipartFile.getOriginalFilename());
        assertEquals(expectedMultipartFile.getContentType(), actualMultipartFile.getContentType());
        assertArrayEquals(expectedMultipartFile.getBytes(), actualMultipartFile.getBytes());
    }

    @Test
    public void test_invalid_base64_image() {
        String invalidBase64Image = "invalidBase64Image";

        assertThrows(IllegalArgumentException.class, () -> {
            mapper.convert(invalidBase64Image);
        });
    }

}
