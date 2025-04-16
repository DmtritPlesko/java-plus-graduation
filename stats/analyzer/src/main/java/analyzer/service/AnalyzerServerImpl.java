package analyzer.service;

import analyzer.model.EventSimilarity;
import analyzer.repozitory.EventSimilarityRepository;
import analyzer.repozitory.UserActionRepository;
import interaction.controller.FeignEventController;
import interaction.dto.event.EventFullDto;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.grpc.stats.recommendation.RecommendationMessage;
import ru.practicum.grpc.stats.recommendation.RecommendationsControllerGrpc;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnalyzerServerImpl extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    static final Double VIEW_WEIGHT = 0.4;
    static final Double REGISTER_WEIGHT = 0.8;
    static final Double LIKE_WEIGHT = 1.0;
    static final long NEIGHBORS_COUNT = 10;
    final UserActionRepository userActionRepository;
    final EventSimilarityRepository eventSimilarityRepository;
    final FeignEventController feignEventController;

    @Override
    public void getRecommendationsForUser(RecommendationMessage.UserPredictionsRequestProto request,
                                          StreamObserver<RecommendationMessage.RecommendedEventProto> responseObserver) {
        long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        Pageable pageable = PageRequest.of(0, maxResults);

        try {
            List<Long> interactedEventIds = userActionRepository
                    .findEventIdByUserIdOrderByTimestamp(userId, pageable)
                    .getContent();

            if (!interactedEventIds.isEmpty()) {
                List<Long> notInteractedEventIds = userActionRepository.findDistinctEventIdByUserIdNot(userId);

                List<Long> mostSimilarEventsIds = eventSimilarityRepository.findMostSimilarEventsIds(
                        interactedEventIds,
                        notInteractedEventIds,
                        pageable);

                Map<Long, Map<Long, Double>> allSimilarities = eventSimilarityRepository
                        .batchFindSimilarEvents(mostSimilarEventsIds, interactedEventIds);

                Set<Long> allEventIds = allSimilarities.values().stream()
                        .flatMap(map -> map.keySet().stream())
                        .collect(Collectors.toSet());
                Map<Long, Double> eventsAndRatings = feignEventController.findEventsByIds(allEventIds)
                        .stream()
                        .collect(Collectors.toMap(
                                EventFullDto::getId,
                                EventFullDto::getRating
                        ));

                mostSimilarEventsIds.stream()
                        .limit(NEIGHBORS_COUNT)
                        .forEach(eventId -> {
                            Map<Long, Double> similarities = allSimilarities.get(eventId);
                            if (similarities != null && !similarities.isEmpty()) {
                                double score = calculateRecommendationScore(similarities, eventsAndRatings);
                                RecommendationMessage.RecommendedEventProto event = buildRecommendationEvent(eventId, score);
                                responseObserver.onNext(event);
                            }
                        });
            }
        } catch (Exception e) {
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getSimilarEvents(RecommendationMessage.SimilarEventsRequestProto request,
                                 StreamObserver<RecommendationMessage.RecommendedEventProto> responseObserver) {

        Set<Long> eventsByUserId = userActionRepository.findDistinctEventIdByUserId(request.getUserId());
        Pageable pageable = PageRequest.of(0, request.getMaxResults());
        List<EventSimilarity> eventSimilarities = eventSimilarityRepository.findSimilaritiesExcludingInteracted(
                request.getEventId(),
                eventsByUserId.stream().toList(),
                pageable).stream().toList();

        long tempSimilarEventId;
        for (EventSimilarity similarity : eventSimilarities) {
            tempSimilarEventId = similarity.getEventIdA() == request.getEventId()
                    ? similarity.getEventIdA() : similarity.getEventIdB();
            RecommendationMessage.RecommendedEventProto eventProto = RecommendationMessage.RecommendedEventProto.newBuilder()
                    .setEventId(tempSimilarEventId)
                    .setScore(similarity.getMaxResult())
                    .build();
            responseObserver.onNext(eventProto);
        }
        responseObserver.onCompleted();


    }

    @Override
    public void getInteractionsCount(RecommendationMessage.InteractionsCountRequestProto request,
                                     StreamObserver<RecommendationMessage.RecommendedEventProto> responseObserver) {
        request.getEventIdList().forEach(eventId -> {

            double likeCount = userActionRepository.countUserIdsWithSpecificActionOnly(
                    eventId,
                    ActionTypeAvro.LIKE.toString(),
                    List.of(ActionTypeAvro.VIEW.toString(), ActionTypeAvro.REGISTER.toString()));

            double registerCount = userActionRepository.countUserIdsWithSpecificActionOnly(
                    eventId,
                    ActionTypeAvro.REGISTER.toString(),
                    List.of(ActionTypeAvro.VIEW.toString(), ActionTypeAvro.LIKE.toString()));

            double viewCount = userActionRepository.countUserIdsWithSpecificActionOnly(
                    eventId,
                    ActionTypeAvro.VIEW.toString(),
                    List.of(ActionTypeAvro.LIKE.toString(), ActionTypeAvro.REGISTER.toString()));

            double score = likeCount * LIKE_WEIGHT
                    + registerCount * REGISTER_WEIGHT
                    + viewCount * VIEW_WEIGHT;
            RecommendationMessage.RecommendedEventProto eventProto = RecommendationMessage
                    .RecommendedEventProto.newBuilder()
                    .setEventId(eventId)
                    .setScore(score)
                    .build();
            responseObserver.onNext(eventProto);
        });
        responseObserver.onCompleted();
    }

    private double calculateRecommendationScore(
            Map<Long, Double> similarities,
            Map<Long, Double> ratings) {

        double weightedSum = 0.0;
        double sumSimilarities = 0.0;

        for (Map.Entry<Long, Double> entry : similarities.entrySet()) {
            Long eventId = entry.getKey();
            Double similarity = entry.getValue();
            Double rating = ratings.getOrDefault(eventId, 0.0);

            weightedSum += similarity * rating;
            sumSimilarities += similarity;
        }

        return sumSimilarities > 0 ? weightedSum / sumSimilarities : 0;
    }

    private RecommendationMessage.RecommendedEventProto buildRecommendationEvent(
            Long eventId,
            double score) {

        return RecommendationMessage.RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(score)
                .build();
    }

}
