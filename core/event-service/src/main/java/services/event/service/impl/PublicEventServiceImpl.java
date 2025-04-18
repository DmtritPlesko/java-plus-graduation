package services.event.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ewm.clients.AnalyzerClient;
import ewm.clients.CollectorClient;
import interaction.controller.FeignRequestController;
import interaction.controller.FeignUserController;
import interaction.dto.event.EventFullDto;
import interaction.dto.event.EventShortDto;
import interaction.dto.event.EventState;
import interaction.dto.event.PublicEventParam;
import interaction.dto.request.RequestStatus;
import interaction.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.grpc.stats.action.UserActionMessage;
import services.category.model.QCategory;
import services.event.mappers.EventMapper;
import services.event.model.Event;
import services.event.model.QEvent;
import services.event.repository.EventRepository;
import services.event.service.PublicEventService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicEventServiceImpl implements PublicEventService {
    final EventRepository eventRepository;
    final EventMapper eventMapper;
    final JPAQueryFactory jpaQueryFactory;
    final FeignRequestController feignRequestController;
    final FeignUserController feignUserController;
    final CollectorClient collectorClient;
    final AnalyzerClient analyzerClient;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllBy(PublicEventParam eventParam, Pageable pageRequest) {
        BooleanBuilder eventQueryExpression = buildExpression(eventParam);
        List<EventShortDto> events = getEvents(pageRequest, eventQueryExpression);
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
    @Transactional(readOnly = true)
    public EventFullDto getBy(long eventId) {
        EventFullDto event = eventRepository.findById(eventId).map(eventMapper::toEventFullDto)
                .orElseThrow(() -> new NotFoundException("Мероприятие с Id =" + eventId + " не найдено"));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие id = " + eventId + " не опубликовано");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusYears(10);

        long confirmedRequests = feignRequestController
                .countAllByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED.name());
        event.setConfirmedRequests(confirmedRequests);
        return event;
    }

    @Override
    public boolean exists(Long id) {
        return eventRepository.existsByCategoryId(id);
    }

    @Override
    public List<Event> findAllByIn(Set<Long> ids) {
        return eventRepository.findAllByIdIn(ids);
    }

    @Override
    public List<EventFullDto> getRecommendations(long userId, int maxResults) {
        return eventRepository.findAllByIdIn(analyzerClient.getRecommendationsForUser(userId, maxResults))
                .stream().map(eventMapper::toEventFullDto).toList();
    }

    @Override
    public void like(long eventId, long userId) {
        if (feignRequestController.isExist(eventId, userId)) {
            collectorClient.sendUserAction(userId, eventId, UserActionMessage.ActionTypeProto.ACTION_LIKE);
        } else {
            throw new NotFoundException("События с id = " + eventId
                    + " и пользователем с id = " + userId + " не найдено");
        }
    }

    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return feignRequestController.getConfirmedRequestMap(eventIds);
    }

    List<EventShortDto> getEvents(Pageable pageRequest, BooleanBuilder eventQueryExpression) {
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

        return events.stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        feignUserController.getAllBuIds(initiatorIds)
                                .get(event.getInitiatorId())))
                .toList();
    }

    BooleanBuilder buildExpression(PublicEventParam eventParam) {
        BooleanBuilder eventQueryExpression = new BooleanBuilder();

        eventQueryExpression.and(QEvent.event.state.eq(EventState.PUBLISHED));
        Optional.ofNullable(eventParam.getRangeStart())
                .ifPresent(rangeStart -> eventQueryExpression.and(QEvent.event.eventDate.after(rangeStart)));
        Optional.ofNullable(eventParam.getRangeEnd())
                .ifPresent(rangeEnd -> eventQueryExpression.and(QEvent.event.eventDate.before(eventParam.getRangeEnd())));
        Optional.ofNullable(eventParam.getPaid())
                .ifPresent(paid -> eventQueryExpression.and(QEvent.event.paid.eq(paid)));
        Optional.ofNullable(eventParam.getCategories())
                .filter(category -> !category.isEmpty())
                .ifPresent(category -> eventQueryExpression.and(QEvent.event.category.id.in(category)));
        Optional.ofNullable(eventParam.getText())
                .filter(text -> !text.isEmpty()).ifPresent(text -> {
                    eventQueryExpression.and(QEvent.event.annotation.containsIgnoreCase(text));
                    eventQueryExpression.or(QEvent.event.description.containsIgnoreCase(text));
                });
        return eventQueryExpression;
    }
}
