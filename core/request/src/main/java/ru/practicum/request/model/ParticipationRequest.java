package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.mappers.Event;
import ru.practicum.request.mappers.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "requests", uniqueConstraints = @UniqueConstraint(columnNames = {"requester_id", "event_id"}))
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "created")
    LocalDateTime created;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    Event event;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    User requester;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 100)
    RequestStatus status = RequestStatus.PENDING;
}
