package ewm.event.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ewm.category.model.QCategory;
import ewm.client.StatRestClientImpl;
import ewm.dto.ViewStatsDto;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.dto.PublicEventParam;
import ewm.event.mappers.EventMapper;
import ewm.event.model.EventState;
import ewm.event.model.QEvent;
import ewm.event.repository.EventRepository;
import ewm.event.service.PublicEventService;
import ewm.exception.NotFoundException;
import ewm.request.model.QParticipationRequest;
import ewm.request.model.RequestStatus;
import ewm.request.repository.RequestRepository;
import ewm.user.model.QUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicEventServiceImpl implements PublicEventService {
    final EventRepository eventRepository;
    final RequestRepository requestRepository;
    final StatRestClientImpl statRestClient;
    final EventMapper eventMapper;
    final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllBy(PublicEventParam eventParam, Pageable pageRequest) {
        BooleanBuilder eventQueryExpression = buildExpression(eventParam);
        List<EventShortDto> events = getEvents(pageRequest, eventQueryExpression);
        List<Long> eventIds = events.stream().map(EventShortDto::getId).toList();
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);

        Set<String> uris = events.stream()

        return events.stream().peek(shortDto -> {
            shortDto.setViews(viewMap.getOrDefault("/events/" + shortDto.getId(), 0L));
            shortDto.setConfirmedRequests(confirmedRequestsMap.getOrDefault(shortDto.getId(), 0L));
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getBy(long eventId) {
        EventFullDto event = eventRepository.findById(eventId).map(eventMapper::toEventFullDto)


        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие id = " + eventId + " не опубликовано");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusYears(10);

        statRestClient.stats(start, now, List.of("/events/" + eventId), true)


        long confirmedRequests = requestRepository.countAllByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedRequests);
        return event;
    }

    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        QParticipationRequest qRequest = QParticipationRequest.participationRequest;

        return jpaQueryFactory

    }

    List<EventShortDto> getEvents(Pageable pageRequest, BooleanBuilder eventQueryExpression) {
        return jpaQueryFactory
    }

    BooleanBuilder buildExpression(PublicEventParam eventParam) {
        BooleanBuilder eventQueryExpression = new BooleanBuilder();

        eventQueryExpression.and(QEvent.event.state.eq(EventState.PUBLISHED));
        Optional.ofNullable(eventParam.getRangeStart())
        return eventQueryExpression;
    }
}
