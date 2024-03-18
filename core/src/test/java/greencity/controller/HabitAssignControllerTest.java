package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import greencity.annotations.CurrentUser;
import greencity.dto.habit.HabitAssignCustomPropertiesDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitAssignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static greencity.ModelUtils.getPrincipal;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class HabitAssignControllerTest {

    @Mock
    private HabitAssignService habitAssignService;



    @InjectMocks
    HabitAssignController habitAssignController;

    private static final XmlMapper xmlMapper = new XmlMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Test
    void assignDefaultTest() throws Exception {
        long habitId = 1L;
        UserVO userVO = new UserVO();
        HabitAssignManagementDto habitAssignManagementDto = new HabitAssignManagementDto();

        when(habitAssignService.assignDefaultHabitForUser(habitId, userVO)).thenReturn(habitAssignManagementDto);

        String expected = "<HabitAssignManagementDto><id/><status/><createDateTime/><habitId/><userId/><duration/><workingDays/><habitStreak/><lastEnrollment/><progressNotificationHasDisplayed/></HabitAssignManagementDto>";
        String actual = mockMvc.perform(post("/habit/assign/{habitId}", habitId))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertEquals(expected, actual);
        verify(habitAssignService, times(1)).assignDefaultHabitForUser(habitId, userVO);
    }

    @Test
    void assignCustomTest() throws Exception {
        long habitId = 1L;
        UserVO userVO = new UserVO();
        String userXml = xmlMapper.writeValueAsString(userVO);
        HabitAssignCustomPropertiesDto habitAssignCustomPropertiesDto = new HabitAssignCustomPropertiesDto();
        List<HabitAssignManagementDto> responseDtoList = List.of(new HabitAssignManagementDto());
        when(habitAssignService.assignCustomHabitForUser(habitId, userVO, habitAssignCustomPropertiesDto))
                .thenReturn(responseDtoList);

        mockMvc.perform(post("/habit/assign/{habitId}/custom", habitId)
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xmlMapper.writeValueAsString(userXml))
                        .with(request -> {
                            request.setAttribute("userVO" , userVO);
                            return request;
                        }))
                .andExpect(status().isCreated());

        verify(habitAssignService, times(1))
                .assignCustomHabitForUser(habitId, userVO, habitAssignCustomPropertiesDto);
    }

    @Test
    void updateHabitAssignDurationTest() throws Exception {
        long habitAssignId = 1L;
        int duration = 30;
        UserVO userVO = new UserVO();
        String userXml = xmlMapper.writeValueAsString(userVO);
        HabitAssignUserDurationDto responseDto = new HabitAssignUserDurationDto();

        when(habitAssignService.updateUserHabitInfoDuration(habitAssignId, userVO.getId(), duration))
                .thenReturn(responseDto);
        mockMvc.perform(put("/habit/assign/{habitAssignId}/update-habit-duration", habitAssignId)
                        .param("duration", String.valueOf(duration))
                        .contentType(MediaType.APPLICATION_XML)
                        .content(userXml))
                .andExpect(status().isOk());

        verify(habitAssignService, times(1)).updateUserHabitInfoDuration(habitAssignId, userVO.getId(), duration);
    }

    @Test
    void getHabitAssignTest() throws Exception{
        Principal principal = Mockito.mock(Principal.class);
        long userId = 2L;
        long habitAssignId = 1L;
        String locale = "en";
        UserVO userVO = new UserVO().setId(userId);
        ObjectMapper objectMapper = new ObjectMapper();

        String userXml = objectMapper.writeValueAsString(userVO);
        System.out.println(userXml);

        MockMultipartFile jsonFile = new MockMultipartFile("request", "", "application/json", userXml.getBytes());

        HabitAssignDto habitAssignDto = new HabitAssignDto().setUserId(userId);

//        when(habitAssignService.getByHabitAssignIdAndUserId(habitAssignId, userVO.getId(), locale)).thenReturn(habitAssignDto);

        mockMvc.perform(multipart("/habit/assign/{habitAssignId}", habitAssignId)

//                .contentType(MediaType.APPLICATION_XML)
//                .content(userXml)
                                .file(jsonFile).principal(principal)
                                .param("locale", "en")
                )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getCurrentUserHabitAssignsByIdAndAcquiredTest() throws Exception {
        long userId = 1L;
        Principal principal = new Principal() {
            long id = userId;
            @Override
            public String getName() {
                return "testName";
            }
            long getId(){
                return id;
            }
        };
        UserVO userVO = new UserVO();
        userVO.setId(1L);
        String locale = "en";
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(userVO));
        System.out.println(xmlMapper.writeValueAsString(userVO));

        List<HabitAssignDto> habitAssignDtos = List.of(new HabitAssignDto(), new HabitAssignDto());
//        doReturn(habitAssignDtos).
                when(habitAssignService.getAllHabitAssignsByUserIdAndStatusNotCancelled(any(), anyString()))
                .thenReturn(habitAssignDtos)
        ;

        mockMvc.perform(get("/habit/assign/allForCurrentUser"))
//                        .param("locale", "en")
//                        .principal(principal))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userVO)))
                .andExpect(status().isOk());
//                .andExpect(content().xml(xmlMapper.writeValueAsString(habitAssignDtos)));

        // Verify
        verify(habitAssignService, times(1)).getAllHabitAssignsByUserIdAndStatusNotCancelled(any(), anyString());
    }

    @Test
    void getCurrentUserHabitAssignsByIdAndAcquired_HabitAssignControllerTest_shouldReturnIsOkStatus() throws Exception {
        when(habitAssignService.getAllHabitAssignsByUserIdAndStatusNotCancelled(any(), anyString()))
                .thenReturn(List.of(new HabitAssignDto()));

        mockMvc.perform(get("/habit/assign/allForCurrentUser"))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(any(), anyString());
    }



}
