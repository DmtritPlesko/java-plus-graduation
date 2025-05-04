package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "created")
    LocalDateTime created;
    @Column(name = "event_id")
    Long eventId;
    @Column(name = "requester_id")
    Long requesterId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100)
    RequestStatus status = RequestStatus.PENDING;
}
