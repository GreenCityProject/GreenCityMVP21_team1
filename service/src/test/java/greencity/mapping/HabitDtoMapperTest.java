package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitDtoMapperTest {

    @InjectMocks
    HabitDtoMapper mapper;

    @Test
    void convert() {
        //
        HabitDto expected = ModelUtils.getHabitDto();
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("")
                .description("")
                .habitItem("")
                .language(ModelUtils.getLanguage())
                .habit(Habit.builder()
                        .id(null)
                        .image("image")
                        .complexity(2)
                        .defaultDuration(2)
                        .shoppingListItems(Collections.emptySet())
                        .tags(Collections.emptySet())
                        .build())
                .build();
        HabitDto actual = mapper.convert(habitTranslation);
        assertEquals(expected, actual);

    }

}
