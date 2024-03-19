package greencity.dto.event;

import greencity.dto.user.AuthorDto;
import greencity.dto.user.UserVO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventDtoRequest {

    @NotEmpty
    @Length(max = 70)
    private String eventTitle;


    @NotEmpty
    private String location;

    @NotEmpty
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotEmpty
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate finishDate;

    @NotEmpty
    private Integer duration;

    @NotEmpty
    @Length(min = 20, max = 63_206)
    private String description;

    @NotEmpty
    private String image;

    @NotEmpty
    private AuthorDto author;

}
