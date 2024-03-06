package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import greencity.dto.habitstatistic.*;
import greencity.enums.HabitRate;
import greencity.service.HabitStatisticService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HabitStatisticControllerTest {
    private static final String habitStatisticLink = "/habit/statistic";
    private static final XmlMapper xmlMapper = new XmlMapper();
    private MockMvc mockMvc;
    @InjectMocks
    private HabitStatisticController habitStatisticController;
    @Mock
    private HabitStatisticService habitService;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @ParameterizedTest
    @CsvSource({"1", "5"})
    void given_habitId_when_findAllByHabitId_then_returnGetHabitStatisticDto(Long id) throws Exception {
        GetHabitStatisticDto mockHabitDto = GetHabitStatisticDto.builder()
                .habitStatisticDtoList(Collections.emptyList())
                .amountOfUsersAcquired(1L)
                .build();

        when(habitService.findAllStatsByHabitId(id)).thenReturn(mockHabitDto);

        mockMvc.perform(get(STR."\{habitStatisticLink}/{habitId}", id))
                .andExpect(status().isOk())
                .andExpect(content().xml(xmlMapper.writeValueAsString(mockHabitDto)));

        verify(habitService).findAllStatsByHabitId(id);
    }

    @ParameterizedTest
    @CsvSource({"1", "5"})
    void given_habitId_when_saveHabitStatistic_then_saveWithStatusCreated(Long userId) throws Exception {
        AddHabitStatisticDto addHabitStatisticDto = new AddHabitStatisticDto(1, HabitRate.DEFAULT, ZonedDateTime.parse("2007-12-09T16:49:01.020Z"));
        String jsonContent = """
                  {
                  "amountOfItems": 1,
                  "createDate": "2007-12-09T16:49:01.020Z",
                  "habitRate": "DEFAULT"
                }
                """;
        mockMvc.perform(post(STR."\{habitStatisticLink}/{habitId}", userId)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(habitService).saveByHabitIdAndUserId(userId, null, addHabitStatisticDto);
    }

    @ParameterizedTest
    @CsvSource({"1", "5"})
    void given_habitAssignId_when_findAllStatsByHabitAssignId_then_returnListOfHabitStatisticDto(Long habitAssignId) throws Exception {
        mockMvc.perform(get(STR."\{habitStatisticLink}/assign/{habitAssignId}", habitAssignId))
                .andExpect(status().isOk());
        verify(habitService).findAllStatsByHabitAssignId(habitAssignId);
    }

    @ParameterizedTest
    @CsvSource({"1", "5"})
    void given_habitStatisticForUpdateDto_when_updateStatistic_then_updateWithStatusOK(Long id) throws Exception {
        UpdateHabitStatisticDto habitStatisticForUpdateDto = new UpdateHabitStatisticDto(1, HabitRate.DEFAULT);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(habitStatisticForUpdateDto);

        mockMvc.perform(put(STR."\{habitStatisticLink}/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());


        verify(habitService).update(id, id, habitStatisticForUpdateDto);
    }

    @Test
    void given_locale_when_getTodayStatisticsForAllHabitItems_then_returnHabitItemsAmountStatisticDtoList() throws Exception {
        HabitItemsAmountStatisticDto habitItemsAmountStatisticDto = HabitItemsAmountStatisticDto.builder()
                .habitItem("item")
                .notTakenItems(1L)
                .build();
        String expectedXmlString = "<List><item><habitItem>item</habitItem><notTakenItems>1</notTakenItems></item></List>";

        when(habitService.getTodayStatisticsForAllHabitItems("en")).thenReturn(Collections.singletonList(habitItemsAmountStatisticDto));

        MvcResult mvcResult = mockMvc.perform(get(STR."\{habitStatisticLink}/todayStatisticsForAllHabitItems")
                        .param("locale", "en"))
                .andExpect(status().isOk())
                .andReturn();

        String xmlContent = mvcResult.getResponse().getContentAsString();

        Assertions.assertEquals(expectedXmlString, xmlContent);
        verify(habitService).getTodayStatisticsForAllHabitItems("en");
    }

    @ParameterizedTest
    @CsvSource({"1", "5"})
    void given_userId_when_findAmountOfAcquiredHabits_then_returnLong(Long id) throws Exception {
        when(habitService.getAmountOfAcquiredHabitsByUserId(id)).thenReturn(id);

        mockMvc.perform(get(STR."\{habitStatisticLink}/acquired/count")
                        .param("userId", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(content().xml(xmlMapper.writeValueAsString(id)));
        verify(habitService).getAmountOfAcquiredHabitsByUserId(id);
    }

    @ParameterizedTest
    @CsvSource({"1", "5"})
    void given_userId_when_findAmountOfHabitsInProgress_then_returnLong(Long id) throws Exception {
        when(habitService.getAmountOfHabitsInProgressByUserId(id)).thenReturn(id);

        mockMvc.perform(get(STR."\{habitStatisticLink}/in-progress/count")
                        .param("userId", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(content().xml(xmlMapper.writeValueAsString(id)));
        verify(habitService).getAmountOfHabitsInProgressByUserId(id);
    }
}