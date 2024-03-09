package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.entity.HabitAssign;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitAssignManagementDtoMapperTest {
    @InjectMocks
    HabitAssignManagementDtoMapper mapper;

    @Test
    void convert() {
        HabitAssignManagementDto expected = ModelUtils.getHabitAssignManagementDto();


        HabitAssign habitAssign = HabitAssign.builder()
                .user(ModelUtils.getUser())
                .habit(ModelUtils.getHabit())
                .status(HabitAssignStatus.ACQUIRED)
                .id(1L)
                .duration(0)
                .workingDays(0)
                .habitStreak(0)
                .build();

        HabitAssignManagementDto actual = mapper.convert(habitAssign);
        assertEquals(expected, actual);
    }
}
