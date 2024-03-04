package greencity.validator;

import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageValidatorTest {
    @InjectMocks
    private LanguageValidator languageValidator;
    @Mock
    private LanguageService languageService;
    @BeforeEach
    void setUp(){
        List<String> codes = Arrays.asList("en", "ua");

        when(languageService.findAllLanguageCodes()).thenReturn(codes);

        languageValidator.initialize(null);
    }



    @Test
    void isValidTrueTest() {
        Locale locale = new Locale("en");

        assertTrue(languageValidator.isValid(locale, null));
    }

    @Test
    void isValidFalseTest() {
        Locale locale = new Locale("JP");

        assertFalse(languageValidator.isValid(locale, null));
    }
}