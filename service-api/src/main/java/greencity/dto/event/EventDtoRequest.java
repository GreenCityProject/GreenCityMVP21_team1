package greencity.dto.event;

import greencity.dto.user.AuthorDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EventDtoRequest {

    public static final int MIN_DESCRIPTION_LENGTH = 20;
    public static final int MAX_DESCRIPTION_LENGTH = 63_206;
    public static final int MAX_TITLE_LENGTH = 70;
    @NotEmpty
    @Length(max = MAX_TITLE_LENGTH)
    private String eventTitle;


    @NotEmpty
    private String location;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate finishDate;

    @NotNull
    private Integer duration;

    @NotEmpty
    @Length(min = MIN_DESCRIPTION_LENGTH, max = MAX_DESCRIPTION_LENGTH)
    private String description;

    @NotEmpty
    private String image;

    @NotNull
    private AuthorDto author;

}
