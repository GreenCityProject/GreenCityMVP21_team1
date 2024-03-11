package greencity.mapping;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.enums.HabitRate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HabitStatisticDtoMapperTest {

    @InjectMocks
    HabitStatisticDtoMapper mapper;

    @Test
    void convert() {
        ZonedDateTime time = ZonedDateTime.now();
        HabitStatisticDto expected = HabitStatisticDto.builder()
                .id(1L)
                .amountOfItems(1)
                .createDate(time)
                .habitRate(HabitRate.NORMAL)
                .habitAssignId(1L)
                .build();

        HabitStatistic habitStatistic = HabitStatistic.builder()
                .id(1L)
                .amountOfItems(1)
                .habitRate(HabitRate.NORMAL)
                .createDate(time)
                .habitAssign(HabitAssign.builder()
                        .id(1L)
                        .build())
                .build();

        HabitStatisticDto actual = mapper.convert(habitStatistic);

        assertEquals(expected, actual);
    }

}
