package analyzer.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "similarity")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "event_id")
    Long eventId;

    @Column(name = "result")
    Float maxResult;

    @Column(name = "time")
    LocalDateTime time;

}
