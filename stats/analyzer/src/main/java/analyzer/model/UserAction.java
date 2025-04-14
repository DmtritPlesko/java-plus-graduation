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
@Table(name = "actions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "event_id")
    Long eventId;

    @Column(name = "action")
    String action;

    @Column(name = "time")
    LocalDateTime time;
}
