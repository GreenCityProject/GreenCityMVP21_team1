package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitService;
import greencity.service.TagsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HabitControllerTest {
//    private static final String habitLink = "/habit";
//    private static final XmlMapper xmlMapper = new XmlMapper();
//    private MockMvc mockMvc;
//    @InjectMocks
//    private HabitController habitController;
//    @Mock
//    private HabitService habitService;
//    @Mock
//    private TagsService tagsService;
//
//    @BeforeEach
//    public void setUp() {
//        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
//    }
//
//    @ParameterizedTest
//    @CsvSource({"1", "5"})
//    void given_validHabitIdAndLocale_when_getHabitById_then_returnHabitDto(Long id) throws Exception {
//        HabitDto mockHabitDto = HabitDto.builder().id(1L).build();
//
//        when(habitService.getByIdAndLanguageCode(id, "en")).thenReturn(mockHabitDto);
//
//        mockMvc.perform(get(STR."\{habitLink}/{id}", id)).andExpect(status().isOk()).andExpect(content().xml(xmlMapper.writeValueAsString(mockHabitDto)));
//
//        verify(habitService).getByIdAndLanguageCode(id, "en");
//    }
//
//    @ParameterizedTest
//    @CsvSource({"1, 2", "5, 10"})
//    void given_userVOAndLocaleAndPageable_when_getAll_then_returnPageableDto(Integer pageNumber, Integer pageSize) throws Exception {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        UserVO userVO = new UserVO();
//        PageableDto<HabitDto> pageableDto = new PageableDto<>(Collections.emptyList(), 0, 0, 0);
//
//        when(habitService.getAllHabitsByLanguageCode(userVO, pageable, "en")).thenReturn(pageableDto);
//
//        mockMvc.perform(get(STR."\{habitLink}").param("page", pageNumber.toString()).param("size", pageSize.toString())).andExpect(status().isOk()).andExpect(content().xml(xmlMapper.writeValueAsString(pageableDto)));
//
//        verify(habitService).getAllHabitsByLanguageCode(userVO, pageable, "en");
//    }
//
//    @ParameterizedTest
//    @CsvSource({"1", "5"})
//    void given_validHabitIdAndLocale_when_getShoppingListItems_then_returnShoppingListItems(Long id) throws Exception {
//        List<ShoppingListItemDto> shoppingListItemDtos = Collections.singletonList(new ShoppingListItemDto(2L, "test", "test"));
//        String expectedXmlString = "<List><item><id>2</id><text>test</text><status>test</status></item></List>";
//
//        when(habitService.getShoppingListForHabit(id, "en")).thenReturn(shoppingListItemDtos);
//
//        MvcResult mvcResult = mockMvc.perform(get(STR."\{habitLink}/{id}/shopping-list", id).param("locale", "en")).andExpect(status().isOk()).andReturn();
//        String xmlContent = mvcResult.getResponse().getContentAsString();
//
//        Assertions.assertEquals(expectedXmlString, xmlContent);
//
//        verify(habitService).getShoppingListForHabit(id, "en");
//    }
//
//    @ParameterizedTest
//    @CsvSource({"1, 2, eco", "5, 10, artificial"})
//    void given_tagsAndLocaleAndPageable_when_getAllByTagsAndLanguageCode_then_returnPageableDto(Integer pageNumber, Integer pageSize, String tag) throws Exception {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        List<String> tags = Collections.singletonList(tag);
//        PageableDto<HabitDto> pageableDto = new PageableDto<>(Collections.emptyList(), 0, 0, 0);
//
//        when(habitService.getAllByTagsAndLanguageCode(pageable, tags, "en")).thenReturn(pageableDto);
//
//        mockMvc.perform(get(STR."\{habitLink}/tags/search").param("tags", tag).param("page", pageNumber.toString()).param("size", pageSize.toString())).andExpect(status().isOk()).andExpect(content().xml(xmlMapper.writeValueAsString(pageableDto)));
//
//        verify(habitService).getAllByTagsAndLanguageCode(pageable, tags, "en");
//    }
//
//    @ParameterizedTest
//    @CsvSource({"1, 2, eco, 7, false", "5, 10, artificial, 5, true"})
//    void given_userVOAndLocaleAndTagsAndCustomHabitAndComplexitiesAndPageable_when_getAllByDifferentParameters_then_returnPageableDto(Integer pageNumber, Integer pageSize, String tag, Integer complexity, Boolean isCustomHabit) throws Exception {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        UserVO userVO = new UserVO();
//        Optional<List<String>> tags = Optional.of(Collections.singletonList(tag));
//        PageableDto<HabitDto> pageableDto = new PageableDto<>(Collections.emptyList(), 0, 0, 0);
//
//        when(habitService.getAllByDifferentParameters(userVO, pageable, tags, Optional.of(isCustomHabit), Optional.of(Collections.singletonList(complexity)), "en")).thenReturn(pageableDto);
//
//        mockMvc.perform(get(STR."\{habitLink}/search")
//                        .param("tags", tag)
//                        .param("isCustomHabit", isCustomHabit.toString())
//                        .param("complexities", complexity.toString())
//                        .param("page", pageNumber.toString())
//                        .param("size", pageSize.toString()))
//                .andExpect(status().isOk())
//                .andExpect(content().xml(xmlMapper.writeValueAsString(pageableDto)));
//
//        verify(habitService).getAllByDifferentParameters(userVO, pageable, tags, Optional.of(isCustomHabit), Optional.of(Collections.singletonList(complexity)), "en");
//    }
//
//    @Test
//    void given_invalidParameters_when_getAllByDifferentParameters_then_throwBadRequestException() {
//        assertThrows(Exception.class, () -> mockMvc.perform(get(STR."\{habitLink}/search"))
//                .andExpect(status().isBadRequest())
//                .andExpect(result -> Assertions.assertEquals("You should enter at least one parameter", Objects.requireNonNull(result.getResolvedException()).getMessage())));
//    }
//
//    @Test
//    void given_validLocale_when_findAllHabitsTags_then_returnListOfTags() throws Exception {
//        List<String> habits = Collections.singletonList("test");
//        String expectedXmlString = "<List><item>test</item></List>";
//
//        when(tagsService.findAllHabitsTags("en")).thenReturn(habits);
//
//        mockMvc.perform(get(STR."\{habitLink}/tags?locale=en")).andExpect(status().isOk()).andExpect(content().xml(expectedXmlString));
//
//        verify(tagsService).findAllHabitsTags("en");
//    }
//
//    @Test
//    void given_validRequestAndImageAndPrincipal_when_addCustomHabit_then_returnAddCustomHabitDtoResponse() throws Exception {
//        Principal principal = Mockito.mock(Principal.class);
//        String json = """
//                        {
//                         "complexity": 1,
//                         "defaultDuration": 1,
//                         "habitTranslations": null,
//                         "customShoppingListItemDto": null,
//                         "image": null,
//                         "tagIds": null
//                        }
//                """;
//
//        ObjectMapper mapper = new ObjectMapper();
//        AddCustomHabitDtoRequest addCustomHabitDtoRequest = mapper.readValue(json, AddCustomHabitDtoRequest.class);
//        AddCustomHabitDtoResponse addCustomHabitDtoResponse = mapper.readValue(json, AddCustomHabitDtoResponse.class);
//
//        MockMultipartFile jsonFile = new MockMultipartFile("request", "", "application/json", json.getBytes());
//
//        when(habitService.addCustomHabit(eq(addCustomHabitDtoRequest), isNull(), eq("Olivia.Johnson@gmail.com"))).thenReturn(addCustomHabitDtoResponse);
//        when(principal.getName()).thenReturn("Olivia.Johnson@gmail.com");
//
//        this.mockMvc.perform(multipart(STR."\{habitLink}/custom")
//                .file(jsonFile).principal(principal)).andExpect(status().isCreated()).andExpect(content().xml(xmlMapper.writeValueAsString(addCustomHabitDtoResponse)));
//
//        verify(habitService).addCustomHabit(eq(addCustomHabitDtoRequest), isNull(), eq("Olivia.Johnson@gmail.com"));
//    }
//
//    @ParameterizedTest
//    @CsvSource({"1", "5"})
//    void given_validHabitIdAndUserVO_when_getFriendsAssignedToHabitProfilePictures_then_returnListOfProfilePictures(Long id) throws Exception {
//        List<UserProfilePictureDto> userProfilePictureDto = Collections.singletonList(new UserProfilePictureDto(id, "test", "test"));
//
//        when(habitService.getFriendsAssignedToHabitProfilePictures(id, null)).thenReturn(userProfilePictureDto);
//
//        String expectedXmlString = STR."<List><item><id>\{id}</id><name>test</name><profilePicturePath>test</profilePicturePath></item></List>";
//
//        mockMvc.perform(get(STR."\{habitLink}/{habitId}/friends/profile-pictures", id)).andExpect(status().isOk()).andExpect(content().xml(expectedXmlString));
//
//        verify(habitService).getFriendsAssignedToHabitProfilePictures(id, null);
//    }
}
