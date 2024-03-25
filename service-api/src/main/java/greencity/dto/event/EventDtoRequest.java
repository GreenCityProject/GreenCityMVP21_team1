package greencity.dto.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;


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
    private String title;

    @NotEmpty
    @Length(min = MIN_DESCRIPTION_LENGTH, max = MAX_DESCRIPTION_LENGTH)
    private String description;

    @NotNull
    private Boolean open = true;


    @NotEmpty
    private List<DatesLocationDto> datesLocations;


    @NotNull
    private List <String> tags;



}
