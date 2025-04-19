package services.event.service;

import interaction.dto.event.EventFullDto;

import java.util.List;
import java.util.Set;

public interface EventService {

    boolean existEventByCategoryId(Long id);

    EventFullDto findById(Long id);

    List<EventFullDto> findEventsByIds(Set<Long> ids);
}
