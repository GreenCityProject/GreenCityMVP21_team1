package greencity.controller;


import greencity.service.LanguageService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)

public class LanguageControllerTest {
    private static final String languageLink = "/language";
    private MockMvc mockMvc;
    @InjectMocks
    private LanguageController languageController;
    @Mock
    private LanguageService languageService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(languageController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

    }


    @Test
    void getAllLanguageCodesTestV2() throws Exception {
        List<String> expected = Arrays.asList("en", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(expected);

        MvcResult result = mockMvc.perform(get(languageLink)).andExpect(status().isOk()).andReturn();

        Assertions.assertTrue(result.getResponse().getContentAsString().contains("ua"));
        Assertions.assertTrue(result.getResponse().getContentAsString().contains("en"));
        verify(languageService, times(1)).findAllLanguageCodes();
    }
}
