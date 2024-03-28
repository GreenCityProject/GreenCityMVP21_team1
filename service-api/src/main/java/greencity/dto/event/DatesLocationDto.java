package greencity.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DatesLocationDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private LocalDateTime finishDate;

    @NotNull
    private CoordinatesDto coordinates;
}
