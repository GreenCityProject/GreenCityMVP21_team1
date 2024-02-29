package greencity.service;

import greencity.constant.AppConstant;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.enums.HabitAssignStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserAlreadyHasHabitAssignedException;
import greencity.exception.exceptions.UserAlreadyHasMaxNumberOfActiveHabitAssigns;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HabitAssignServiceImplTest {
    @Mock
    private HabitAssignRepo habitAssignRepo;

    @Mock
    private HabitRepo habitRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ShoppingListItemRepo shoppingListItemRepo;

    @Mock
    private UserShoppingListItemRepo userShoppingListItemRepo;

    @Mock
    private CustomShoppingListItemRepo customShoppingListItemRepo;

    @Mock
    private ShoppingListItemTranslationRepo shoppingListItemTranslationRepo;

    @Mock
    private HabitStatusCalendarRepo habitStatusCalendarRepo;

    @Mock
    private ShoppingListItemService shoppingListItemService;

    @Mock
    private CustomShoppingListItemService customShoppingListItemService;

    @Mock
    private HabitStatisticService habitStatisticService;

    @Mock
    private HabitStatusCalendarService habitStatusCalendarService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    HabitAssignServiceImpl habitAssignService;


    @Test
    void getByHabitAssignIdAndUserId_habitAssignIdNotExists_throwException() {
        Long nonExistedHabitAssignId = 1L;
        Mockito.when(habitAssignRepo.findById(nonExistedHabitAssignId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> habitAssignService.getByHabitAssignIdAndUserId(
                nonExistedHabitAssignId, 1L, "Java"));
    }



    @Test
    void getByHabitAssignIdAndUserId_userIdNotEquals_throwException() {
        Long unexpectedUserId = 1L;
        Long existedHabitAssignId = 2L;
        User user = new User();
        user.setId(unexpectedUserId);
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setId(existedHabitAssignId);
        habitAssign.setUser(user);

        Mockito.when(habitAssignRepo.findById(existedHabitAssignId)).thenReturn(Optional.of(habitAssign));

        assertThrows(UserHasNoPermissionToAccessException.class,
                () -> habitAssignService.getByHabitAssignIdAndUserId(
                        existedHabitAssignId, 3L, "Java"
                ));
    }


    @Test
    void getByHabitAssignIdAndUserId_habitTranslationDoesNotEqualsLanguage_throwException() {
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setId(1L);

        User user = new User();
        user.setId(2L);
        habitAssign.setUser(user);

        Language language = new Language();
        language.setCode("Java");

        List<HabitTranslation> habitTranslations = new ArrayList<>();
        habitTranslations.add(HabitTranslation.builder().language(language).build());

        habitAssign.setHabit(Habit.builder().habitTranslations(habitTranslations).build());
        Mockito.when(habitAssignRepo.findById(1L)).thenReturn(Optional.of(habitAssign));
        assertThrows(NotFoundException.class, () -> habitAssignService
                .getByHabitAssignIdAndUserId(1L, 2L, "JS"));

    }


    @Test
    void assignDefaultHabitForUser_checkStatusInProgressExists_throwException() {
        Long userVOId = 2L;
        Long habitId = 1L;
        Long habitAssignId = 3L;
        Habit habit = new Habit();
        habit.setId(habitId);
        UserVO userVO = new UserVO();
        userVO.setId(userVOId);
        HabitAssign habitAssign = HabitAssign.builder().habit(habit).build();
        habitAssign.setId(habitAssignId);
        habitAssign.setStatus(HabitAssignStatus.INPROGRESS);

        List<HabitAssign> habitAssigns = new ArrayList<>();
        habitAssigns.add(habitAssign);

        Mockito.when(habitAssignRepo.findAllByUserId(userVOId)).thenReturn(habitAssigns);

        assertThrows(UserAlreadyHasHabitAssignedException.class, () -> habitAssignService
                .assignDefaultHabitForUser(habitId, userVO));
    }

    @Test
    void assignDefaultHabitForUser_findHabitByNotExistsId_throwException() {
        Long userVOId = 2L;
        Long existedHabitId = 1L;
        Long nonExistedHabitId = 33L;
        Long habitAssignId = 3L;
        Habit habit = new Habit();
        habit.setId(existedHabitId);
        UserVO userVO = new UserVO();
        userVO.setId(userVOId);
        HabitAssign habitAssign = HabitAssign.builder().habit(habit).build();
        habitAssign.setId(habitAssignId);
        habitAssign.setStatus(HabitAssignStatus.ACTIVE);
        List<HabitAssign> habitAssigns = new ArrayList<>();
        habitAssigns.add(habitAssign);

        Mockito.when(habitAssignRepo.findAllByUserId(userVOId)).thenReturn(habitAssigns);
        Mockito.when(habitRepo.findById(nonExistedHabitId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> habitAssignService.assignDefaultHabitForUser(33L, userVO));

    }

    @Test
    void assignDefaultHabitForUser_validateHabitForAssign_userHasMaxNumberOfHabit_throwException() {
        Long userVOId = 2L;
        Long existedUserId = 4L;
        Long habitId = 1L;
        Long habitAssignId = 3L;
        Habit habit = new Habit();
        habit.setId(habitId);
        UserVO userVO = new UserVO();
        userVO.setId(userVOId);
        HabitAssign habitAssign = HabitAssign.builder().habit(habit).build();
        habitAssign.setId(habitAssignId);
        habitAssign.setStatus(HabitAssignStatus.ACTIVE);
        List<HabitAssign> habitAssigns = new ArrayList<>();
        habitAssigns.add(habitAssign);

        Mockito.when(habitAssignRepo.findAllByUserId(userVOId)).thenReturn(habitAssigns);
        Mockito.when(habitRepo.findById(habitId)).thenReturn(Optional.of(habit));


        Mockito.when(modelMapper.map(userVO, User.class)).thenReturn(User.builder().id(existedUserId).build());
        Mockito.when(habitAssignRepo.countHabitAssignsByUserIdAndAcquiredFalseAndCancelledFalse(existedUserId))
                .thenReturn(AppConstant.MAX_NUMBER_OF_HABIT_ASSIGNS_FOR_USER);

        assertThrows(UserAlreadyHasMaxNumberOfActiveHabitAssigns.class,
                () -> habitAssignService.assignDefaultHabitForUser(1L, userVO));
    }

    @Test
    void assignDefaultHabitForUser_validateHabitForAssign_userAlreadyHasHabitAssigned_throwException() {
        Long userVOId = 2L;
        Long existedUserId = 4L;
        Long habitId = 1L;
        Long habitAssignId = 3L;
        Habit habit = new Habit();
        habit.setId(habitId);
        UserVO userVO = new UserVO();
        userVO.setId(userVOId);
        HabitAssign habitAssign = HabitAssign.builder().habit(habit).build();
        habitAssign.setId(habitAssignId);
        habitAssign.setStatus(HabitAssignStatus.ACTIVE);
        List<HabitAssign> habitAssigns = new ArrayList<>();
        habitAssigns.add(habitAssign);

        String instantExpected = "2024-02-23T09:33:52Z";
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(instantExpected);

        try (MockedStatic<ZonedDateTime> mockedLocalDateTime = Mockito.mockStatic(ZonedDateTime.class)) {
            mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(zonedDateTime);

            Mockito.when(habitAssignRepo.findAllByUserId(userVOId)).thenReturn(habitAssigns);
            Mockito.when(habitRepo.findById(habitId)).thenReturn(Optional.of(habit));

            Mockito.when(modelMapper.map(userVO, User.class)).thenReturn(User.builder().id(existedUserId).build());
            Mockito.when(habitAssignRepo.findByHabitIdAndUserIdAndCreateDate(habitId, existedUserId, zonedDateTime))
                    .thenReturn(Optional.of(habitAssign));
            assertThrows(UserAlreadyHasHabitAssignedException.class, () -> habitAssignService
                    .assignDefaultHabitForUser(habitId, userVO));
        }

    }
    //Todo


}
