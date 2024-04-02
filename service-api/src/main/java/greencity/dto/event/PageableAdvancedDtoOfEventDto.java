package greencity.dto.event;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "page")
@Builder
@EqualsAndHashCode
public class PageableAdvancedDtoOfEventDto {


    private Integer currentPage;

    private List<EventDto> page;

    private Long totalElements;
}
