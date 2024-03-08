package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitTranslation;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitAssignDtoMapperTest {

    @InjectMocks
    HabitAssignDtoMapper mapper;

    @Test
    void convert() {
        //
        HabitAssignDto expected = ModelUtils.getHabitAssignDto();
        HabitAssign habitAssign = HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.ACQUIRED)
                .habit(Habit.builder()
                        .id(1L)
                        .image("")
                        .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                                .id(1L)
                                .name("")
                                .description("")
                                .habitItem("")
                                .language(ModelUtils.getLanguage())
                                .build()))
                        .build())
                .user(ModelUtils.getUser())
                .userShoppingListItems(Collections.emptyList())
                .workingDays(0)
                .duration(0)
                .habitStreak(0)
                .habitStatistic(Collections.emptyList())
                .habitStatusCalendars(Collections.emptyList())
                .build();

        HabitAssignDto actual = mapper.convert(habitAssign);
        assertEquals(expected, actual);
    }
}
