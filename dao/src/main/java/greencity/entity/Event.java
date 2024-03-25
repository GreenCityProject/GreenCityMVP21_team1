package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "events")
@ToString(exclude = {"author", "tags"})
@EqualsAndHashCode(exclude = {"author", "tags"})

public class Event {
    private final static Boolean EVENT_DEFAULT_STATUS = true;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    @Size(max = 70)
    private String title;

    @Column(name = "description", nullable = false)
    @Size(min = 20, max = 63_206)
    private String description;

    @Column(name = "open", nullable = false)
    private Boolean open = EVENT_DEFAULT_STATUS;

    @Column
    private String image;

    @OneToMany(mappedBy = "event")
    private List<DateLocation> dateLocation;

    @ManyToMany
    @JoinTable(name = "events_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @ManyToOne
    @JoinColumn(name = "author")
    private User author;


}
