package event.service;


import event.model.Event;
import interaction.dto.event.EventFullDto;
import org.springframework.data.domain.Pageable;
import interaction.dto.event.EventShortDto;
import interaction.dto.event.PublicEventParam;

import java.util.List;
import java.util.Set;

public interface PublicEventService {
    List<EventShortDto> getAllBy(PublicEventParam publicEventParam, Pageable pageRequest);

    EventFullDto getBy(long eventId);

    boolean exists(Long id);

    List<Event> findAllByIn(Set<Long> ids);
}
