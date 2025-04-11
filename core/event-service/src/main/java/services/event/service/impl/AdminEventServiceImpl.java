package services.event.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ewm.client.StatRestClient;
import ewm.dto.ViewStatsDto;
import interaction.controller.FeignRequestController;
import interaction.controller.FeignUserController;
import interaction.dto.event.AdminEventParam;
import interaction.dto.event.EventFullDto;
import interaction.dto.event.EventState;
import interaction.dto.event.UpdateEventAdminRequest;
import interaction.dto.user.UserShortDto;
import interaction.exception.ConflictException;
import interaction.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import services.category.model.QCategory;
import services.event.mappers.EventMapper;
import services.event.mappers.UserMapper;
import services.event.model.Event;
import services.event.model.QEvent;
import services.event.repository.EventRepository;
import services.event.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEventServiceImpl implements AdminEventService {
    final EventRepository eventRepository;
    final EventMapper eventMapper;
    final JPAQueryFactory jpaQueryFactory;
    final StatRestClient statRestClient;
    final FeignRequestController requestController;
    final FeignUserController feignUserController;
    final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllBy(AdminEventParam eventParam, Pageable pageRequest) {
        BooleanBuilder eventQueryExpression = buildBooleanExpression(eventParam);

        List<EventFullDto> events = getEvents(pageRequest, eventQueryExpression);
        List<Long> eventIds = events.stream().map(EventFullDto::getId).toList();
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);

        UserShortDto userShortDto = feignUserController
                .getAllBy(eventParam.getUsers(), 0, 10)
                .stream()
                .map(userMapper::toUserShortDto)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Set<String> uris = events.stream()
                .map(event -> "/events/" + event.getId()).collect(Collectors.toSet());

        LocalDateTime end = events
                .stream()
                .min(Comparator.comparing(EventFullDto::getEventDate))
                .orElseThrow(() -> new NotFoundException("Даты не заданы"))
                .getEventDate();

        Map<String, Long> viewMap = statRestClient
                .stats(LocalDateTime.now(), end, uris.stream().toList(), false).stream()
                .collect(Collectors.groupingBy(ViewStatsDto::getUri, Collectors.summingLong(ViewStatsDto::getHits)));

        return events.stream().peek(shortDto -> {
            shortDto.setViews(viewMap.getOrDefault("/events/" + shortDto.getId(), 0L));
            shortDto.setInitiator(userShortDto);
            shortDto.setConfirmedRequests(confirmedRequestsMap.getOrDefault(shortDto.getId(), 0L));
        }).toList();
    }

    @Override
    @Transactional
    public EventFullDto updateBy(long eventId, UpdateEventAdminRequest updateEventUserRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с с id = " + eventId + " не найдено"));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие" + event.getId() + "уже опубликовано");
        }
        if (event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException("Нельзя опубликовать отмененное событие");
        }
        eventRepository.save(eventMapper.toUpdatedEvent(event, updateEventUserRequest, event.getCategory()));
        return eventMapper.toEventFullDto(event);
    }

    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return requestController.getConfirmedRequestMap(eventIds);
    }

    private List<EventFullDto> getEvents(Pageable pageRequest, BooleanBuilder eventQueryExpression) {
        List<Event> events = jpaQueryFactory
                .selectFrom(QEvent.event)
                .leftJoin(QEvent.event.category, QCategory.category)
                .fetchJoin()
                .where(eventQueryExpression)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .stream()
                .toList();
        List<Long> initiatorIds = events.stream().map(Event::getInitiatorId).toList();
        Map<Long, UserShortDto> initiators = feignUserController.getAllBuIds(initiatorIds);

        return events.stream()
                .map(event -> eventMapper.toEventFullDto(event, initiators.get(event.getInitiatorId())))
                .toList();
    }

    BooleanBuilder buildBooleanExpression(AdminEventParam eventParam) {
        BooleanBuilder eventQueryExpression = new BooleanBuilder();

        QEvent qEvent = QEvent.event;
        Optional.ofNullable(eventParam.getUsers())
                .ifPresent(userIds -> eventQueryExpression.and(qEvent.initiatorId.in(userIds)));
        Optional.ofNullable(eventParam.getStates())
                .ifPresent(userStates -> eventQueryExpression.and(qEvent.state.in(userStates)));
        Optional.ofNullable(eventParam.getCategories())
                .ifPresent(categoryIds -> eventQueryExpression.and(qEvent.category.id.in(categoryIds)));
        Optional.ofNullable(eventParam.getRangeStart())
                .ifPresent(rangeStart -> eventQueryExpression.and(qEvent.eventDate.after(rangeStart)));
        Optional.ofNullable(eventParam.getRangeEnd())
                .ifPresent(rangeEnd -> eventQueryExpression.and(qEvent.eventDate.before(rangeEnd)));
        return eventQueryExpression;
    }
}
