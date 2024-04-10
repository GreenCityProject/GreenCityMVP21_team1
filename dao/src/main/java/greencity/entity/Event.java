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
@ToString(exclude = {"author", "tags", "dateLocation"})
@EqualsAndHashCode(exclude = {"author", "tags"})

public class Event {
    private static final Boolean EVENT_DEFAULT_STATUS = true;

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

    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "images", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "image", nullable = false)
    private List<String> images;

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
