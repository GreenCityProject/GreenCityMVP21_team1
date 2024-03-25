package greencity.dto.event;


import greencity.dto.tag.TagDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "author")
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
    private List<DatesLocationDto> datesLocationDtos;

    @NotNull
    private List <String>  tags;



    //{"title":"Test"
    // ,"description":"<p>jhvblkjbk;jhbjvhjckhbkjnkjhjhvhgchcjbk</p>"
    // ,"open":true,
    // "datesLocations":[{"startDate":"2024-03-25T18:00:00Z","finishDate":"2024-03-25T20:00:00Z","coordinates":{"latitude":50.38616051958162,"longitude":30.43901304687499}}],
    // "tags":["Environmental"]}
}
