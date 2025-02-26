package greencity.dto.event;

import greencity.dto.tag.TagUaEnDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "datesLocationDtos")
@Builder
@EqualsAndHashCode
public class EventDto {
    public static final int MIN_DESCRIPTION_LENGTH = 20;
    public static final int MAX_DESCRIPTION_LENGTH = 63_206;
    public static final int MAX_TITLE_LENGTH = 70;

    @NotNull
    @Length(min = 1)
    private Long id;

    @NotEmpty
    @Length(max = MAX_TITLE_LENGTH)
    private String title;

    @NotEmpty
    @Length(min = MIN_DESCRIPTION_LENGTH, max = MAX_DESCRIPTION_LENGTH)
    private String description;

    @NotNull
    private Boolean open = true;

    @NotEmpty
    private List<String>images;

    @NotEmpty
    private List<DatesLocationDto> datesLocationDtos;

    @NotNull
    private List<TagUaEnDto> tags;
}
