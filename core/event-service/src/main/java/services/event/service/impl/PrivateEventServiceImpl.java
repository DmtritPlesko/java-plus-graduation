package services.event.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ewm.client.StatRestClientImpl;
import interaction.controller.FeignRequestController;
import interaction.controller.FeignUserController;
import interaction.dto.event.*;
import interaction.dto.user.UserDto;
import interaction.exception.ConflictException;
import interaction.exception.NotFoundException;
import interaction.exception.PermissionException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.category.mapper.CategoryMapper;
import services.category.model.Category;
import services.category.model.QCategory;
import services.category.service.PublicCategoryService;
import services.event.mappers.EventMapper;
import services.event.mappers.User;
import services.event.mappers.UserMapper;
import services.event.model.Event;
import services.event.model.QEvent;
import services.event.repository.EventRepository;
import services.event.service.PrivateEventService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateEventServiceImpl implements PrivateEventService {
    final PublicCategoryService categoryService;
    final EventRepository eventRepository;
    final CategoryMapper categoryMapper;
    final EventMapper eventMapper;
    final StatRestClientImpl statRestClient;
    final JPAQueryFactory jpaQueryFactory;
    final UserMapper userMapper;
    final FeignUserController userController;
    final FeignRequestController feignRequestController;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllBy(long userId, Pageable pageRequest) {

        BooleanExpression booleanExpression = QEvent.event.initiatorId.eq(userId);
        List<EventShortDto> events = getEvents(pageRequest, booleanExpression);
        List<Long> eventIds = events.stream().map(EventShortDto::getId).toList();
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);

        Set<String> uris = events.stream()
                .map(event -> "/events/" + event.getId()).collect(Collectors.toSet());

        LocalDateTime end = events
                .stream()
                .min(Comparator.comparing(EventShortDto::getEventDate))
                .orElseThrow(() -> new NotFoundException("Даты не заданы"))
                .getEventDate();

        return events.stream().peek(shortDto -> {
            shortDto.setConfirmedRequests(confirmedRequestsMap.getOrDefault(shortDto.getId(), 0L));
        }).toList();
    }

    @Override
    @Transactional
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        User initiator = userMapper.toUser(userController.getAllBy(List.of(userId), 0, 10).getFirst());
        Category category = categoryMapper.toCategory(categoryService.getBy(newEventDto.getCategory()));
        Event event = eventMapper.toEvent(newEventDto, initiator, category);
        eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setInitiator(userMapper.toUserShortDto(initiator));
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getBy(long userId, long eventId) {
        EventFullDto eventFullDto = eventRepository.findById(eventId).map(eventMapper::toEventFullDto)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        UserDto userDto = userController.getBy(userId);
        eventFullDto.setInitiator(userMapper.toUserShortDto(userDto));
        if (eventFullDto.getInitiator().getId() != userId) {
            throw new PermissionException("Доступ запрещен");
        }
        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateBy(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с с id = " + eventId + " не найдено"));
        if (event.getInitiatorId() != userId) {
            throw new PermissionException("Доступ запрещен");
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя отменить опубликованное событие");
        }
        Category category = categoryMapper.toCategory(categoryService.getBy(event.getCategory().getId()));
        return eventMapper.toEventFullDto(eventMapper.toUpdatedEvent(event, updateEventUserRequest, category));
    }

    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return feignRequestController.getConfirmedRequestMap(eventIds);
    }

    List<EventShortDto> getEvents(Pageable pageRequest, BooleanExpression eventQueryExpression) {
        List<Event> events = jpaQueryFactory
                .selectFrom(QEvent.event)
                .leftJoin(QEvent.event.category, QCategory.category)
                .fetchJoin()
                .where(eventQueryExpression)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .stream()
                .toList();

        List<Long> listInitiatorIds = events.stream().map(Event::getInitiatorId).toList();

        return events.stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        userController.getAllBuIds(listInitiatorIds)
                                .get(event.getInitiatorId())))
                .toList();
    }
}
