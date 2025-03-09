package event.service;

import interaction.dto.event.EventFullDto;
import interaction.dto.event.NewEventDto;
import interaction.dto.event.UpdateEventUserRequest;
import org.springframework.data.domain.Pageable;
import interaction.dto.event.EventShortDto;

import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getAllBy(long userId, Pageable pageRequest);

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto getBy(long userId, long eventId);

    EventFullDto updateBy(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);
}
