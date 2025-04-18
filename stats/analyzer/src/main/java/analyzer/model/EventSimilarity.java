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
@Table(name = "similarities")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_id_a")
    Long eventIdA;

    @Column(name = "event_id_b")
    Long eventIdB;

    @Column(name = "result")
    double maxResult;

    @Column(name = "time")
    LocalDateTime time;

}
