package services.event.service;

import interaction.dto.event.EventFullDto;

public interface EventService {

    boolean existEventByCategoryId(Long id);

    EventFullDto findById(Long id);
}
