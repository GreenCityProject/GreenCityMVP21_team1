package greencity.service;

import greencity.constant.AppConstant;
import greencity.dto.language.LanguageDTO;
import greencity.entity.Language;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LanguageServiceImplTest {
    @Mock
    private LanguageRepo languageRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LanguageServiceImpl languageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllLanguages_returnList() {
        Long languageId = 1L;
        String languageCode = "ua";
        List<Language> languages = new ArrayList<>();
        List<LanguageDTO> languageDTOS = new ArrayList<>();
        languageDTOS.add(new LanguageDTO(languageId, languageCode));
        Language language = new Language();
        language.setId(languageId);
        language.setCode(languageCode);
        languages.add(language);

        Mockito.when(languageRepo.findAll()).thenReturn(languages);
        Mockito.when(modelMapper.map(languages, new TypeToken<List<LanguageDTO>>() {
        }.getType())).thenReturn(languageDTOS);

        List<LanguageDTO> allLanguages = languageService.getAllLanguages();

        LanguageDTO languageDTO = allLanguages.get(0);
        assertEquals(1, allLanguages.size());
        assertEquals(languageId, languageDTO.getId());
        assertEquals(languageCode, languageDTO.getCode());
    }

    @Test
    void extractLanguageCodeFromRequest_languageCodeIsNull_returnDefaultLanguageCode() {
        Mockito.when(request.getParameter("language")).thenReturn(null);
        String result = languageService.extractLanguageCodeFromRequest();
        assertEquals(AppConstant.DEFAULT_LANGUAGE_CODE, result);
    }

    @Test
    void extractLanguageCodeFromRequest_languageCodeIsNotNull_returnLanguageCode() {
        Mockito.when(request.getParameter("language")).thenReturn("en");
        String result = languageService.extractLanguageCodeFromRequest();
        assertEquals("en", result);
    }

    @Test
    void findByCode_languageWasNotFound_throwException() {
        String unexpectedCode = "en";
        Mockito.when(languageRepo.findByCode(unexpectedCode)).thenReturn(Optional.empty());

        assertThrows(LanguageNotFoundException.class, () -> languageService.findByCode(unexpectedCode));
    }

    @Test
    void findByCode_languageWasFound_returnLanguage() {
        String expectedCode = "en";
        Language language = new Language();
        language.setCode(expectedCode);
        LanguageDTO expectedLanguageDTO = new LanguageDTO(1L, expectedCode);
        Mockito.when(languageRepo.findByCode(expectedCode)).thenReturn(Optional.of(language));
        Mockito.when(modelMapper.map(language, LanguageDTO.class)).thenReturn(expectedLanguageDTO);

        LanguageDTO languageDTO = languageService.findByCode(expectedCode);

        assertEquals(expectedLanguageDTO.getCode(), languageDTO.getCode());
    }

    @Test
    void findAllLanguageCodes_returnList() {
        String expectedCode = "en";
        Mockito.when(languageRepo.findAllLanguageCodes()).thenReturn(List.of(expectedCode));
        List<String> result = languageService.findAllLanguageCodes();
        assertEquals(1, result.size());
    }

    @Test
    void findByTagTranslationId_languageNotHaveTranslationId_throwException() {
        Long unexpectedId = 1L;
        Mockito.when(languageRepo.findByTagTranslationId(unexpectedId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> languageService.findByTagTranslationId(unexpectedId));
    }

    @Test
    void findByTagTranslationId_languageHasTranslationId_returnModelDto() {
        Long expectedId = 1L;
        Language language = new Language();
        language.setId(expectedId);
        LanguageDTO languageDTO = new LanguageDTO();
        languageDTO.setId(expectedId);
        Mockito.when(languageRepo.findByTagTranslationId(expectedId)).thenReturn(Optional.of(language));
        Mockito.when(modelMapper.map(language, LanguageDTO.class)).thenReturn(languageDTO);
        LanguageDTO result = languageService.findByTagTranslationId(expectedId);

        assertEquals(languageDTO, result);
    }
}
