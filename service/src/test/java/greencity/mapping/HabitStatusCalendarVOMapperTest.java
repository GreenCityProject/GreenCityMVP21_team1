package greencity.mapping;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitStatusCalendarVOMapperTest {

    @InjectMocks
    HabitStatusCalendarVOMapper mapper;

    @Test
    void convert() {
        LocalDate date = LocalDate.now();

        HabitStatusCalendarVO expected = HabitStatusCalendarVO.builder()
                .enrollDate(date)
                .id(1L)
                .habitAssignVO(HabitAssignVO.builder()
                        .id(1L)
                        .build())
                .build();

        HabitStatusCalendar habitStatusCalendar = HabitStatusCalendar.builder()
                .id(1L)
                .enrollDate(date)
                .habitAssign(HabitAssign.builder()
                        .id(1L)
                        .build())
                .build();

        HabitStatusCalendarVO actual = mapper.convert(habitStatusCalendar);

        assertEquals(expected, actual);
    }
}
