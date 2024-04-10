package greencity.dto.tag;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
public class TagUaEnDto {
    private Long id;
    private String nameUa;
    private String nameEn;
}
