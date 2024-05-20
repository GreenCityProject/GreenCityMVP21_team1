package greencity.service;

import greencity.converters.DateService;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.GetHabitStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatisticRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HabitStatisticServiceImplTest {
    @Mock
    private HabitStatisticRepo habitStatisticRepo;
    @Mock
    private HabitAssignRepo habitAssignRepo;
    @Mock
    private HabitRepo habitRepo;
    @Mock
    private DateService dateService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private HabitStatisticServiceImpl habitStatisticService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveByHabitIdAndUserId_habitStatisticAlreadyExist_throwException() {
        Long habitId = 1L;
        Long userId = 2L;
        AddHabitStatisticDto dto = new AddHabitStatisticDto();
        HabitStatistic habitStatistic = new HabitStatistic();
        ZonedDateTime dtoCreateDate = dto.getCreateDate();

        Mockito.when(habitStatisticRepo.findStatByDateAndHabitIdAndUserId(dtoCreateDate, habitId, userId))
            .thenReturn(Optional.of(habitStatistic));
        Assertions.assertThrows(NotSavedException.class,
            () -> habitStatisticService.saveByHabitIdAndUserId(habitId, userId, dto));
    }

    @Test
    void saveByHabitIdAndUserId_dtoIsProceedAndHabitAssignNotFoundByHabitIdAndUserId_throwException() {
        Long habitId = 1L;
        Long userId = 2L;
        AddHabitStatisticDto dto = new AddHabitStatisticDto();
        dto.setCreateDate(ZonedDateTime.now());
        HabitStatistic habitStatistic = new HabitStatistic();
        ZonedDateTime dtoCreateDate = dto.getCreateDate();

        Mockito.when(habitStatisticRepo.findStatByDateAndHabitIdAndUserId(dtoCreateDate, habitId, userId))
            .thenReturn(Optional.empty());
        Mockito.when(dateService.convertToDatasourceTimezone(dtoCreateDate)).thenReturn(dtoCreateDate);
        Mockito.when(modelMapper.map(dto, HabitStatistic.class)).thenReturn(habitStatistic);
        Mockito.when(habitAssignRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
            () -> habitStatisticService.saveByHabitIdAndUserId(habitId, userId, dto));

    }

    @Test
    void saveByHabitIdAndUserId_dtoIsProceedAndHabitAssignWasFoundByHabitIdAndUserId_returnHabitStatisticDto() {
        Long habitId = 1L;
        Long userId = 2L;
        Long habitStatisticId = 3L;
        AddHabitStatisticDto dto = new AddHabitStatisticDto();
        dto.setCreateDate(ZonedDateTime.now());
        HabitStatistic habitStatistic = new HabitStatistic();
        habitStatistic.setId(habitStatisticId);
        ZonedDateTime dtoCreateDate = dto.getCreateDate();
        HabitAssign habitAssign = new HabitAssign();
        HabitStatisticDto habitStatisticDto = new HabitStatisticDto();
        habitStatisticDto.setId(habitStatisticId);

        Mockito.when(habitStatisticRepo.findStatByDateAndHabitIdAndUserId(dtoCreateDate, habitId, userId))
            .thenReturn(Optional.empty());
        Mockito.when(dateService.convertToDatasourceTimezone(dtoCreateDate)).thenReturn(dtoCreateDate);
        Mockito.when(modelMapper.map(dto, HabitStatistic.class)).thenReturn(habitStatistic);
        Mockito.when(habitAssignRepo.findByHabitIdAndUserId(habitId, userId)).thenReturn(Optional.of(habitAssign));

        Mockito.when(habitStatisticRepo.save(habitStatistic)).thenReturn(habitStatistic);
        Mockito.when(modelMapper.map(habitStatistic, HabitStatisticDto.class)).thenReturn(habitStatisticDto);

        HabitStatisticDto savedHabitStatisticDto = habitStatisticService.saveByHabitIdAndUserId(habitId, userId, dto);
        Assertions.assertEquals(habitStatisticId, savedHabitStatisticDto.getId());
    }

    @Test
    void saveByHabitIdAndUserId_habitStatisticAlreadyExistAndDtoIsProceed_throwException() {
        Long habitId = 1L;
        Long userId = 2L;
        Long habitStatisticId = 3L;
        AddHabitStatisticDto dto = new AddHabitStatisticDto();
        ZonedDateTime dtoCreateDate = ZonedDateTime.now().minusDays(5);
        dto.setCreateDate(dtoCreateDate);
        HabitStatistic habitStatistic = new HabitStatistic();
        habitStatistic.setId(habitStatisticId);
        HabitAssign habitAssign = new HabitAssign();
        HabitStatisticDto habitStatisticDto = new HabitStatisticDto();
        habitStatisticDto.setId(habitStatisticId);

        Mockito.when(habitStatisticRepo.findStatByDateAndHabitIdAndUserId(dtoCreateDate, habitId, userId))
            .thenReturn(Optional.empty());
        Mockito.when(dateService.convertToDatasourceTimezone(dtoCreateDate)).thenReturn(dtoCreateDate);

        Mockito.verify(habitAssignRepo, Mockito.times(0)).save(habitAssign);
        Assertions.assertThrows(BadRequestException.class,
            () -> habitStatisticService.saveByHabitIdAndUserId(habitId, userId, dto));
    }

    @Test
    void update_habitStatisticNotFoundById_throwException() {
        Long habitStatisticId = 1L;
        Long userId = 2L;
        UpdateHabitStatisticDto dto = new UpdateHabitStatisticDto();

        Mockito.when(habitStatisticRepo.findById(habitStatisticId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
            () -> habitStatisticService.update(habitStatisticId, userId, dto));
    }

    @Test
    void update_habitStatisticUserIdEqualsUserId_throwException() {
        Long habitStatisticId = 1L;
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setUser(user);
        UpdateHabitStatisticDto dto = new UpdateHabitStatisticDto();
        HabitStatistic habitStatistic = new HabitStatistic();
        habitStatistic.setHabitAssign(habitAssign);

        Mockito.when(habitStatisticRepo.findById(habitStatisticId)).thenReturn(Optional.of(habitStatistic));
        Mockito.when(habitStatisticRepo.save(habitStatistic)).thenReturn(habitStatistic);
        Mockito.when(modelMapper.map(habitStatistic, UpdateHabitStatisticDto.class)).thenReturn(dto);

        Assertions.assertEquals(dto, habitStatisticService.update(habitStatisticId, userId, dto));
    }

    @Test
    void update_habitStatisticUserIdNotEqualsUserId_throwException() {
        Long habitStatisticId = 1L;
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setUser(user);
        UpdateHabitStatisticDto dto = new UpdateHabitStatisticDto();
        HabitStatistic habitStatistic = new HabitStatistic();
        habitStatistic.setHabitAssign(habitAssign);

        Mockito.when(habitStatisticRepo.findById(habitStatisticId)).thenReturn(Optional.of(habitStatistic));
        Mockito.verify(habitStatisticRepo, Mockito.times(0)).save(habitStatistic);

        Assertions.assertThrows(BadRequestException.class,
            () -> habitStatisticService.update(habitStatisticId, 3L, dto));

    }

    @Test
    void findById_throwException() {
        Long nonExistedId = 1L;

        Mockito.when(habitStatisticRepo.findById(nonExistedId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> habitStatisticService.findById(nonExistedId));
    }

    @Test
    void findById_returnHabitStatisticDto() {
        Long existedId = 1L;
        HabitStatistic habitStatistic = new HabitStatistic();
        habitStatistic.setId(existedId);
        HabitStatisticDto habitStatisticDto = new HabitStatisticDto();
        habitStatisticDto.setId(existedId);

        Mockito.when(habitStatisticRepo.findById(existedId)).thenReturn(Optional.of(habitStatistic));
        Mockito.when(modelMapper.map(habitStatistic, HabitStatisticDto.class)).thenReturn(habitStatisticDto);
        HabitStatisticDto result = habitStatisticService.findById(existedId);

        Assertions.assertEquals(existedId, result.getId());
    }

    @Test
    void findAllStatsByHabitAssignId_habitAssignNotFound_throwException() {
        Long notExistedId = 1L;

        Mockito.when(habitAssignRepo.findById(notExistedId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
            () -> habitStatisticService.findAllStatsByHabitAssignId(notExistedId));

    }

    @Test
    void findAllStatsByHabitAssignId_habitAssignWasFound_returnListHabitStatisticDto() {
        Long existedId = 1L;
        HabitAssign habitAssign = new HabitAssign();
        habitAssign.setId(existedId);
        List<HabitStatistic> habitStatistics = new ArrayList<>();
        HabitStatisticDto habitStatisticDto = new HabitStatisticDto();

        List<HabitStatisticDto> habitStatisticDtos = new ArrayList<>();
        habitStatisticDtos.add(habitStatisticDto);

        Mockito.when(habitAssignRepo.findById(existedId)).thenReturn(Optional.of(habitAssign));
        Mockito.when(habitStatisticRepo.findAllByHabitAssignId(existedId)).thenReturn(habitStatistics);
        Mockito.when(modelMapper.map(habitStatistics, new TypeToken<List<HabitStatisticDto>>() {
        }.getType())).thenReturn(habitStatisticDtos);

        List<HabitStatisticDto> result = habitStatisticService.findAllStatsByHabitAssignId(existedId);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findAllStatsByHabitId_habitNotFound_throwException() {
        Long notExistedId = 1L;

        Mockito.when(habitRepo.findById(notExistedId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
            () -> habitStatisticService.findAllStatsByHabitAssignId(notExistedId));
    }

    @Test
    void findAllStatsByHabitId_returnGetHabitStatisticDto() {
        Long habitId = 1L;
        Habit habit = new Habit();
        habit.setId(habitId);
        Long amountOfUsersAcquired = 2L;
        List<HabitStatistic> habitStatisticList = new ArrayList<>();
        HabitStatistic habitStatistic = new HabitStatistic();
        habitStatisticList.add(habitStatistic);
        HabitStatisticDto habitStatisticDto = new HabitStatisticDto();
        List<HabitStatisticDto> habitStatisticDtoList = new ArrayList<>();
        habitStatisticDtoList.add(habitStatisticDto);

        Mockito.when(habitRepo.findById(habitId)).thenReturn(Optional.of(habit));
        Mockito.when(habitAssignRepo.findAmountOfUsersAcquired(habitId)).thenReturn(amountOfUsersAcquired);
        Mockito.when(habitStatisticRepo.findAllByHabitId(habitId)).thenReturn(habitStatisticList);
        Mockito.when(modelMapper.map(habitStatistic, HabitStatisticDto.class)).thenReturn(habitStatisticDto);

        GetHabitStatisticDto result = habitStatisticService.findAllStatsByHabitId(habitId);
        Assertions.assertEquals(amountOfUsersAcquired, result.getAmountOfUsersAcquired());
        Assertions.assertEquals(habitStatisticDtoList, result.getHabitStatisticDtoList());
    }

    // todo finish getTodayStatisticsForAllHabitItems test
    @Test
    void getAmountOfHabitsInProgressByUserId_returnAmountOfHabitsInProgress() {
        Long userId = 1L;
        Long expectedId = 3L;

        Mockito.when(habitStatisticRepo.getAmountOfHabitsInProgressByUserId(userId)).thenReturn(expectedId);
        Long result = habitStatisticService.getAmountOfHabitsInProgressByUserId(userId);

        Assertions.assertEquals(expectedId, result);
    }

    @Test
    void getAmountOfAcquiredHabitsByUserId_returnAmountOfAcquiredHabits() {
        Long userId = 1L;
        Long expectedId = 3L;

        Mockito.when(habitStatisticRepo.getAmountOfAcquiredHabitsByUserId(userId)).thenReturn(expectedId);
        Long result = habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId);

        Assertions.assertEquals(expectedId, result);
    }

    @Test
    void deleteAllStatsByHabitAssign_allStatsAreDeleted() {
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        Long habitAssignVOId = 1L;
        habitAssignVO.setId(habitAssignVOId);
        HabitStatistic habitStatistic = new HabitStatistic();
        List<HabitStatistic> habitStatisticList = new ArrayList<>();
        habitStatisticList.add(habitStatistic);

        Mockito.when(habitStatisticRepo.findAllByHabitAssignId(habitAssignVOId)).thenReturn(habitStatisticList);

        habitStatisticService.deleteAllStatsByHabitAssign(habitAssignVO);

        Mockito.verify(habitStatisticRepo, Mockito.times(1)).delete(habitStatistic);
    }

}
