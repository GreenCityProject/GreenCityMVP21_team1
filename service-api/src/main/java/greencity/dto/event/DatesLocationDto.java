package greencity.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DatesLocationDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssxxx")
    private OffsetDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssxxx")
    private OffsetDateTime finishDate;

    @NotNull
    private CoordinatesDto coordinates;
}
