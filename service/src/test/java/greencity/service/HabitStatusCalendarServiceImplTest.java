package greencity.service;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import greencity.repository.HabitStatusCalendarRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HabitStatusCalendarServiceImplTest {
    @Mock
    private HabitStatusCalendarRepo habitStatusCalendarRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private HabitStatusCalendarServiceImpl habitStatusCalendarService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findHabitStatusCalendarByEnrollDateAndHabitAssign_calendarIsNull_returnNull() {
        LocalDate date = LocalDate.now();
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        HabitAssign habitAssign = new HabitAssign();

        Mockito.when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        Mockito.when(habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitAssign(date, habitAssign))
            .thenReturn(null);

        Assertions.assertNull(
            habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitAssign(date, habitAssignVO));
    }

    @Test
    void findHabitStatusCalendarByEnrollDateAndHabitAssign_calendarIsNotNull_returnHabitStatusCalendarVO() {
        LocalDate date = LocalDate.now();
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        HabitAssign habitAssign = new HabitAssign();
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar();

        Mockito.when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        Mockito.when(habitStatusCalendarRepo.findHabitStatusCalendarByEnrollDateAndHabitAssign(date, habitAssign))
            .thenReturn(habitStatusCalendar);
        HabitStatusCalendarVO result = modelMapper.map(habitStatusCalendar, HabitStatusCalendarVO.class);
        Assertions.assertEquals(result,
            habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitAssign(date, habitAssignVO));
    }

    @Test
    void save_returnHabitStatusCalendarVO() {
        HabitStatusCalendarVO habitStatusCalendarVO = new HabitStatusCalendarVO();
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar();
        HabitStatusCalendar saved = habitStatusCalendarRepo.save(habitStatusCalendar);

        Mockito.when(modelMapper.map(habitStatusCalendarVO, HabitStatusCalendar.class)).thenReturn(habitStatusCalendar);
        Mockito.when(modelMapper.map(saved, HabitStatusCalendarVO.class)).thenReturn(habitStatusCalendarVO);
        Assertions.assertEquals(habitStatusCalendarVO, habitStatusCalendarService.save(habitStatusCalendarVO));
    }

    @Test
    void delete_habitStatusCalendarIsDeleted() {
        HabitStatusCalendarVO habitStatusCalendarVO = new HabitStatusCalendarVO();
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar();
        Mockito.when(modelMapper.map(habitStatusCalendarVO, HabitStatusCalendar.class)).thenReturn(habitStatusCalendar);

        habitStatusCalendarService.delete(habitStatusCalendarVO);
        Mockito.verify(habitStatusCalendarRepo, Mockito.times(1)).delete(habitStatusCalendar);
    }

    @Test
    void findTopByEnrollDateAndHabitAssign_returnTopByEnrollmentDateAndHabitAssign() {
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        HabitAssign habitAssign = new HabitAssign();
        LocalDate result = habitStatusCalendarRepo.findTopByEnrollDateAndHabitAssign(habitAssign);

        Mockito.when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        Assertions.assertEquals(result, habitStatusCalendarService.findTopByEnrollDateAndHabitAssign(habitAssignVO));
    }

    @Test
    void findEnrolledDatesAfter_returnListEnrolledDatesAfter() {

        LocalDate date = LocalDate.of(2024, 2, 28);
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        HabitAssign habitAssign = new HabitAssign();

        List<HabitStatusCalendar> habitStatusCalendars = new ArrayList<>();
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar();
        habitStatusCalendar.setEnrollDate(date);
        habitStatusCalendars.add(habitStatusCalendar);
        List<LocalDate> expectedDates = new LinkedList<>();
        expectedDates.add(date);

        Mockito.when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        Mockito.when(habitStatusCalendarRepo.findAllByEnrollDateAfterAndHabitAssign(date, habitAssign))
            .thenReturn(habitStatusCalendars);
        Assertions.assertEquals(expectedDates, habitStatusCalendarService.findEnrolledDatesAfter(date, habitAssignVO));
    }

    @Test
    void findEnrolledDatesBefore_returnListEnrolledDatesBefore() {
        LocalDate date = LocalDate.of(2024, 02, 28);
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        HabitAssign habitAssign = new HabitAssign();

        List<HabitStatusCalendar> habitStatusCalendars = new ArrayList<>();
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar();
        habitStatusCalendar.setEnrollDate(date);
        habitStatusCalendars.add(habitStatusCalendar);
        List<LocalDate> expectedDates = new LinkedList<>();
        expectedDates.add(date);

        Mockito.when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        Mockito.when(habitStatusCalendarRepo.findAllByEnrollDateBeforeAndHabitAssign(date, habitAssign))
            .thenReturn(habitStatusCalendars);
        Assertions.assertEquals(expectedDates, habitStatusCalendarService.findEnrolledDatesBefore(date, habitAssignVO));
    }

    @Test
    void deleteAllByHabitAssign_allHabitStatusCalendarIsDeleted() {
        HabitAssignVO habitAssignVO = new HabitAssignVO();
        HabitAssign habitAssign = new HabitAssign();

        Mockito.when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        habitStatusCalendarService.deleteAllByHabitAssign(habitAssignVO);
        Mockito.verify(habitStatusCalendarRepo, Mockito.times(1)).deleteAllByHabitAssign(habitAssign);
    }

}
