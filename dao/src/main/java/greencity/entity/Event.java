package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "events")
@EqualsAndHashCode
@ToString(exclude = "author")
public class Event {
    private final static String eventDefaultStatus = "opened";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "event_title", nullable = false)
    @Size(max = 70)
    private String eventTitle;

    @Column(name = "start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss.zzz")
    private LocalDateTime startDate;

    @Column(name = "finish_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss.zzz")
    private LocalDateTime finishDate;

    @Column(name = "duration", nullable = false)
    private LocalDateTime duration ;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "description", nullable = false)
    @Size(min = 20, max = 63_206)
    private String description;

    @Column(name = "status", nullable = false)
    private String status = eventDefaultStatus;

    @Column(name = "image", nullable = false)
    private String image;

    @ManyToOne
    @JoinColumn(name = "author")
    private User author;

}
