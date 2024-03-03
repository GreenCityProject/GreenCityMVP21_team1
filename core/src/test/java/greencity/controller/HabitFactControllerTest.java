package greencity.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import greencity.dto.PageableDto;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactTranslationUpdateDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.enums.FactOfDayStatus;
import greencity.mapping.HabitFactDtoResponseMapper;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HabitFactControllerTest {

    @Mock
    private HabitFactService habitFactService;

    @InjectMocks
    private HabitFactController habitFactController;

    private MockMvc mockMvc;

    private static XmlMapper xmlMapper;

    private ModelMapper modelMapper;

    @Mock
    private Validator mockValidator;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitFactController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mockValidator)
                .build();
        xmlMapper = new XmlMapper();
        initModelMapper();
    }

    private void initModelMapper(){
        modelMapper = new ModelMapper();
        modelMapper
                .getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        modelMapper.addConverter(new HabitFactDtoResponseMapper());
        Field field;
        try {
            field = HabitFactController.class.getDeclaredField("mapper");
            field.setAccessible(true);
            field.set(habitFactController, modelMapper);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @ParameterizedTest
    @CsvSource({"2", "5"})
    void getRandomFactByHabitIdTest(long id) throws Exception {
        LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO(
                new LanguageDTO(id, "en"), "Use a towel instead of paper towels and napkins");
        String expectedJson = xmlMapper.writeValueAsString(languageTranslationDTO);
        when(habitFactService.getRandomHabitFactByHabitIdAndLanguage(id,"en")).thenReturn(languageTranslationDTO);

        mockMvc.perform(get("/facts/random/{habitId}", id))
                .andExpect(status().isOk())
                .andExpect(content().xml(expectedJson));
        verify(habitFactService, times(1)).getRandomHabitFactByHabitIdAndLanguage(id, "en");
    }

    @Test
    void getHabitFactOfTheDayTest() throws Exception {
        long languageId = 2L;
        long habitId= 1L;
        LanguageTranslationDTO languageTranslationDTO = new LanguageTranslationDTO(
                new LanguageDTO(habitId, "en"), "Use a towel instead of paper towels and napkins");
        String expectedJson = xmlMapper.writeValueAsString(languageTranslationDTO);

        when(habitFactService.getHabitFactOfTheDay(languageId)).thenReturn(languageTranslationDTO);

        mockMvc.perform(get("/facts/dayFact/{languageId}", languageId))
                .andExpect(status().isOk())
                .andExpect(content().xml(expectedJson));
        verify(habitFactService, times(1)).getHabitFactOfTheDay(languageId);
    }

    @Test
    void getAllTest() throws Exception {
        LanguageTranslationDTO habitFact = new LanguageTranslationDTO(
                new LanguageDTO(1L, "en"), "Use a towel instead of paper towels and napkins");
        PageableDto<LanguageTranslationDTO> pageableDto = new PageableDto<>(
                Collections.singletonList(habitFact), 1, 1, 1);
        String expected = xmlMapper.writeValueAsString(pageableDto);

        when(habitFactService.getAllHabitFacts(PageRequest.of(0, 10), "en")).thenReturn(pageableDto);
        String response = mockMvc.perform(get("/facts")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, response);
    }

    @Test
    void saveTest() throws Exception {
        HabitFactPostDto habitFactPostDto = new HabitFactPostDto(List.of(new LanguageTranslationDTO(
                new LanguageDTO(1L, "en"), "Use a towel instead of paper towels and napkins")),
                new HabitIdRequestDto(2L));
        String habitFactPostRequest = xmlMapper.writeValueAsString(habitFactPostDto);
        String expected = xmlMapper.writeValueAsString(modelMapper.map(habitFactPostDto, HabitFactDtoResponse.class));
        HabitFactVO habitFactVO = modelMapper.map(habitFactPostDto, HabitFactVO.class);

        when(habitFactService.save(habitFactPostDto)).thenReturn(habitFactVO);

        String response = mockMvc.perform(post("/facts").contentType(MediaType.APPLICATION_XML_VALUE).content(habitFactPostRequest))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, response);
    }

    @Test
    void updateTest() throws Exception {
        long habitFactId = 3L;
        HabitFactUpdateDto habitFactUpdateDto = new HabitFactUpdateDto(
                List.of(new HabitFactTranslationUpdateDto(FactOfDayStatus.CURRENT, new LanguageDTO(2L, "en"), "Use a towel instead of paper towels and napkins"))
                , new HabitIdRequestDto(2L));
        HabitFactVO habitFactVO = modelMapper.map(habitFactUpdateDto, HabitFactVO.class);
        HabitFactPostDto expectedDto = modelMapper.map(habitFactVO, HabitFactPostDto.class);

        when(habitFactService.update(habitFactUpdateDto, habitFactId)).thenReturn(habitFactVO);
        String expected = xmlMapper.writeValueAsString(expectedDto);
        String requestXml = xmlMapper.writeValueAsString(habitFactUpdateDto);

        String response = mockMvc.perform(put("/facts/{id}", habitFactId).contentType(MediaType.APPLICATION_XML_VALUE).content(requestXml))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(expected, response);
    }

    @Test
    void deleteTest() throws Exception {
        long habitFactId = 3L;
        when(habitFactService.delete(habitFactId)).thenReturn(habitFactId);

        mockMvc.perform(delete("/facts/{id}", habitFactId))
                .andExpect(status().isOk());
        verify(habitFactService, times(1)).delete(habitFactId);
    }

}
