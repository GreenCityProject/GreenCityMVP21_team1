package greencity.mapping;

import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitAssignUserDurationDtoMapperTest {

    @InjectMocks
    HabitAssignUserDurationDtoMapper mapper;

    @Test
    void convert() {
        HabitAssignUserDurationDto expected = HabitAssignUserDurationDto.builder()
                .habitAssignId(1L)
                .userId(1L)
                .habitId(1L)
                .status(HabitAssignStatus.ACQUIRED)
                .workingDays(0)
                .duration(0)
                .build();

        HabitAssign habitAssign = HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.ACQUIRED)
                .habit(Habit.builder()
                        .id(1L)
                        .image(null)
                        .habitTranslations(null)
                        .build())
                .user(User.builder().id(1L).build())
                .workingDays(0)
                .duration(0)
                .build();

        HabitAssignUserDurationDto actual = mapper.convert(habitAssign);
        assertEquals(expected, actual);
    }
}
