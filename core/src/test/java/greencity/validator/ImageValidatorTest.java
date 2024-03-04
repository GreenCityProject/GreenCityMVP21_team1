package greencity.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageValidatorTest {
    @InjectMocks
    private ImageValidator imageValidator;

    @Mock
    MultipartFile imageMock;

    @BeforeEach
    void setUp() {
        // Perform initialization before each test
        imageValidator.initialize(null);
    }

    @Test
    void isValidTrueTest() {
        // Create a mock MultipartFile for JPEG image
        when(imageMock.getContentType()).thenReturn("image/jpeg");
        assertTrue(imageValidator.isValid(imageMock, null));

        // Create a mock MultipartFile for PNG image
        when(imageMock.getContentType()).thenReturn("image/png");
        assertTrue(imageValidator.isValid(imageMock, null));

        // Create a mock MultipartFile for JPG image
        when(imageMock.getContentType()).thenReturn("image/jpg");
        assertTrue(imageValidator.isValid(imageMock, null));
    }

    @Test
    void isValidFalseTest() {
        // Create a mock MultipartFile for non-image file
        when(imageMock.getContentType()).thenReturn("application/pdf");
        assertFalse(imageValidator.isValid(imageMock, null));
    }
}