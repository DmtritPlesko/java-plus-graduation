package event.service;


import interaction.dto.event.EventFullDto;
import org.springframework.data.domain.Pageable;
import interaction.dto.event.EventShortDto;
import interaction.dto.event.PublicEventParam;

import java.util.List;

public interface PublicEventService {
    List<EventShortDto> getAllBy(PublicEventParam publicEventParam, Pageable pageRequest);

    EventFullDto getBy(long eventId);
}
