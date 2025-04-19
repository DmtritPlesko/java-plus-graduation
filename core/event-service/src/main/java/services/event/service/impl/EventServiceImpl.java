package services.event.service.impl;

import interaction.controller.FeignUserController;
import interaction.dto.event.EventFullDto;
import interaction.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import services.event.mappers.EventMapper;
import services.event.mappers.UserMapper;
import services.event.model.Event;
import services.event.repository.EventRepository;
import services.event.service.EventService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {

    final EventRepository repository;
    final EventMapper mapper;
    final UserMapper userMapper;
    final FeignUserController userController;

    @Override
    public boolean existEventByCategoryId(Long id) {
        return repository.existsByCategoryId(id);
    }

    @Override
    public EventFullDto findById(Long id) {

        Optional<Event> event = repository.findById(id);
        if (event.isEmpty()) {
            throw new NotFoundException("Событие с id = " + id + " не найдено");
        }
        EventFullDto eventFullDto = mapper.toEventFullDto(event.get());
        eventFullDto.setInitiator(userMapper.toUserShortDto(userController.getBy(event.get().getInitiatorId())));

        return eventFullDto;
    }

    @Override
    public List<EventFullDto> findEventsByIds(Set<Long> ids) {
        return repository.findAllByIdIn(ids).stream()
                .map(mapper::toEventFullDto)
                .toList();
    }
}

