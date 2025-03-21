package services.event.service.impl;

import interaction.dto.event.EventFullDto;
import interaction.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import services.event.mappers.EventMapper;
import services.event.repository.EventRepository;
import services.event.service.EventService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {

    final EventRepository repository;
    final EventMapper mapper;

    @Override
    public boolean existEventByCategoryId(Long id) {
        return repository.existsByCategoryId(id);
    }

    @Override
    public EventFullDto findById(Long id) {
        return mapper.toEventFullDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не найдено")));
    }
}

