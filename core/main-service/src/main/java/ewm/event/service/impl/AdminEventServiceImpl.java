package ewm.event.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ewm.category.model.Category;
import ewm.category.model.QCategory;
import ewm.client.StatRestClient;
import ewm.dto.ViewStatsDto;
import ewm.event.dto.AdminEventParam;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.UpdateEventAdminRequest;
import ewm.event.mappers.EventMapper;
import ewm.event.model.Event;
import ewm.event.model.EventState;
import ewm.event.model.QEvent;
import ewm.event.repository.EventRepository;
import ewm.event.service.AdminEventService;
import ewm.exception.ConflictException;
import ewm.exception.NotFoundException;
import ewm.request.model.QParticipationRequest;
import ewm.request.model.RequestStatus;
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
public class AdminEventServiceImpl implements AdminEventService {
    final EventRepository eventRepository;
    final EventMapper eventMapper;
    final JPAQueryFactory jpaQueryFactory;
    final StatRestClient statRestClient;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllBy(AdminEventParam eventParam, Pageable pageRequest) {
        BooleanBuilder eventQueryExpression = buildBooleanExpression(eventParam);

        List<EventFullDto> events = getEvents(pageRequest, eventQueryExpression);
        List<Long> eventIds = events.stream().map(EventFullDto::getId).toList();
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);

        Set<String> uris = events.stream()

        return events.stream().peek(shortDto -> {
            shortDto.setViews(viewMap.getOrDefault("/events/" + shortDto.getId(), 0L));
            shortDto.setConfirmedRequests(confirmedRequestsMap.getOrDefault(shortDto.getId(), 0L));
        }).toList();
    }

    @Override
    @Transactional
    public EventFullDto updateBy(long eventId, UpdateEventAdminRequest updateEventUserRequest) {
        Event event = eventRepository.findById(eventId)
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие" + event.getId() + "уже опубликовано");
        }
        if (event.getState().equals(EventState.CANCELED)) {
            throw new ConflictException("Нельзя опубликовать отмененное событие");
        }

        Category category = event.getCategory();
        eventRepository.save(eventMapper.toUpdatedEvent(event, updateEventUserRequest, category));
        return eventMapper.toEventFullDto(event);
    }

    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        QParticipationRequest qRequest = QParticipationRequest.participationRequest;
        return jpaQueryFactory

    }

    List<EventFullDto> getEvents(Pageable pageRequest, BooleanBuilder eventQueryExpression) {
        return jpaQueryFactory

    }

    BooleanBuilder buildBooleanExpression(AdminEventParam eventParam) {
        BooleanBuilder eventQueryExpression = new BooleanBuilder();

        QEvent qEvent = QEvent.event;
        Optional.ofNullable(eventParam.getUsers())

        return eventQueryExpression;
    }
}
