package greencity.dto.event;


import greencity.dto.user.AuthorDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "author")
@Builder
@EqualsAndHashCode
public class EventDto {

    @NotNull
    @Length(min = 1)
    private Long id;

    @NotEmpty
    @Length(max = 70)
    private String eventTitle;

    @NotEmpty
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss.zzz")
    private LocalDateTime startDate;

    @NotEmpty
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss.zzz")
    private LocalDateTime finishDate;

    @NotEmpty
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss.zzz")
    private LocalDateTime duration;

    @NotEmpty
    private String location;

    @NotEmpty
    @Length(min = 20, max = 63_206)
    private String description;

    @NotEmpty
    private String status;

    @NotEmpty
    private String image;

    @NotEmpty
    private AuthorDto author;
}
