package greencity.mapping;

import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habittranslation.HabitTranslationManagementDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitManagementDtoMapperTest {

    @InjectMocks
    HabitManagementDtoMapper mapper;

    @Test
    void convert() {

        HabitManagementDto expected = HabitManagementDto.builder()
                .id(1L)
                .habitTranslations(Collections.singletonList(HabitTranslationManagementDto.builder()
                        .id(1L)
                        .description("test")
                        .habitItem("item")
                        .name("name")
                        .languageCode("en")
                        .build()))
                .image("image")
                .complexity(1)
                .defaultDuration(1)
                .build();

        Habit habit = Habit.builder()
                .defaultDuration(1)
                .complexity(1)
                .id(1L)
                .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                        .habitItem("item")
                        .name("name")
                        .language(Language.builder()
                                        .code("en")
                                        .build())
                        .description("test")
                        .id(1L)
                        .build()))
                .image("image")
                .build();

        HabitManagementDto actual = mapper.convert(habit);

        assertEquals(expected, actual);
    }

}
