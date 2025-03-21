package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(long userId);

    List<ParticipationRequest> findAllByEventId(long eventId);

    List<ParticipationRequest> findAllByIdInAndEventIdIs(List<Long> eventIds, long eventId);

    long countAllByEventIdAndStatusIs(long eventId, RequestStatus status);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);
}
