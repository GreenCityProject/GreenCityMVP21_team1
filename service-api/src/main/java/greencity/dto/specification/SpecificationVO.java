package greencity.dto.specification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SpecificationVO {
    private Long id;
    private String name;
}
