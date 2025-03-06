package ru.practicum.request.service.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.request.model.QParticipationRequest;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.service.RequestService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl  implements RequestService {
    @Override
    public Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        QParticipationRequest qRequest = QParticipationRequest.participationRequest;

        final JPAQueryFactory jpaQueryFactory = null;

        return jpaQueryFactory
                .select(qRequest.id.as("eventId"), qRequest.count().as("confirmedRequests"))
                .from(qRequest)
                .where(qRequest.id.in(eventIds).and(qRequest.status.eq(RequestStatus.CONFIRMED)))
                .groupBy(qRequest.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> Optional.ofNullable(tuple.get(1, Long.class)).orElse(0L))
                );
    }
}
