package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitAssignMapperTest {

    @InjectMocks
    HabitAssignMapper mapper;

    @Test
    void convert() {
        ZonedDateTime date =ZonedDateTime.now();

        HabitAssign expected = HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.ACQUIRED)
                .createDate(date)
                .habit(Habit.builder()
                        .id(1L)
                        .image(null)
                        .habitTranslations(null)
                        .build())
                .user(null)
                .userShoppingListItems(new ArrayList<>())
                .workingDays(0)
                .duration(null)
                .habitStreak(0)
                .lastEnrollmentDate(date)
                .build();


        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
                .habit(HabitDto.builder().id(1L).build())
                .status(HabitAssignStatus.ACQUIRED)
                .id(1L)
                .duration(null)
                .workingDays(0)
                .habitStreak(0)
                .userShoppingListItems(Collections.emptyList())
                .createDateTime(date)
                .lastEnrollmentDate(date)
                .build();

        HabitAssign actual = mapper.convert(habitAssignDto);
        assertEquals(expected, actual);
    }
}
