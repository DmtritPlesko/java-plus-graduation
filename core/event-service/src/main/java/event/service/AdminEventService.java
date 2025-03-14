package event.service;

import interaction.dto.event.AdminEventParam;
import interaction.dto.event.EventFullDto;
import interaction.dto.event.UpdateEventAdminRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> getAllBy(AdminEventParam eventParam, Pageable pageRequest);

    EventFullDto updateBy(long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
