package services.event.service;


import interaction.dto.event.EventFullDto;
import interaction.dto.event.EventShortDto;
import interaction.dto.event.PublicEventParam;
import org.springframework.data.domain.Pageable;
import services.event.model.Event;

import java.util.List;
import java.util.Set;

public interface PublicEventService {
    List<EventShortDto> getAllBy(PublicEventParam publicEventParam, Pageable pageRequest);

    EventFullDto getBy(long eventId);

    boolean exists(Long id);

    List<Event> findAllByIn(Set<Long> ids);

    List<EventFullDto> getRecommendations(long userId, int maxResults);

    void like(long eventId, long userId);
}
