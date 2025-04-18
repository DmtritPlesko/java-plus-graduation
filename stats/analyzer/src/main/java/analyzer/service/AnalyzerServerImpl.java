package analyzer.service;

import analyzer.model.EventSimilarity;
import analyzer.model.RecommendedEvent;
import analyzer.model.UserAction;
import analyzer.repository.EventSimilarityRepository;
import analyzer.repository.UserActionRepository;
import interaction.controller.FeignEventController;
import interaction.dto.event.EventFullDto;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.recommendation.RecommendationMessage;
import ru.practicum.grpc.stats.recommendation.RecommendationsControllerGrpc;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Qualifier("baseConsumer")
    final KafkaConsumer<String, EventSimilarityAvro> similarityKafkaConsumer;
    @Qualifier("actionsConsumer")
    final KafkaConsumer<String, UserActionAvro> actionsKafkaConsumer;

    final EventSimilarityRepository similarityRepository;
    final UserActionRepository actionRepository;

    @Value("${kafka.topics.similarity}")
    private String similarityTopic;

    @Value("${kafka.topics.actions}")
    private String actionsTopic;

    public void start() {
        similarityKafkaConsumer.subscribe(Collections.singletonList(similarityTopic));
        actionsKafkaConsumer.subscribe(Collections.singletonList(actionsTopic));

        while (true) {
            ConsumerRecords<String, EventSimilarityAvro> similarityRecords = similarityKafkaConsumer.poll(Duration.ofMillis(1000));

            for (ConsumerRecord<String, EventSimilarityAvro> record : similarityRecords) {
                EventSimilarityAvro eventSimilarityAvro = record.value();
                EventSimilarity similarity = new EventSimilarity();
                similarity.setEventIdA(eventSimilarityAvro.getEventA());
                similarity.setEventIdB(eventSimilarityAvro.getEventB());
                similarity.setMaxResult(eventSimilarityAvro.getScore());
                similarity.setTime(eventSimilarityAvro
                        .getTimestamp()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());

            }
            similarityKafkaConsumer.commitSync();

            ConsumerRecords<String, UserActionAvro> actionRecords = actionsKafkaConsumer.poll(Duration.ofMillis(1000));

            for (ConsumerRecord<String, UserActionAvro> record : actionRecords) {
                UserActionAvro userActionAvro = record.value();
                UserAction action = new UserAction();
                action.setEventId(userActionAvro.getEventId());
                action.setUserId(userActionAvro.getUserId());
                action.setAction(userActionAvro.getActionType().toString());
                action.setTime(userActionAvro
                        .getTimestamp()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());

            }
            actionsKafkaConsumer.commitSync();
        }
    }

    @Override
    public void getRecommendationsForUser(RecommendationMessage.UserPredictionsRequestProto request,
                                          StreamObserver<RecommendationMessage.RecommendedEventProto> responseObserver) {
        long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        Pageable pageable = PageRequest.of(0, maxResults);

        try {
            List<Long> interactedEventIds = userActionRepository
                    .findActionsByUserIdOrderByTimestamp(userId, pageable)
                    .getContent();

            if (!interactedEventIds.isEmpty()) {
                List<Long> notInteractedEventIds = userActionRepository.findNotInteractedEventIdsByUserId(userId);

                List<Long> mostSimilarEventsIds = eventSimilarityRepository.findMostSimilarEventsIds(
                        interactedEventIds,
                        notInteractedEventIds,
                        pageable);

                for (int i = 0; i < mostSimilarEventsIds.size()
                        && i < NEIGHBORS_COUNT; i++) {
                    Map<Long, Double> eventsAndSimilarities = similarityRepository
                            .findSimilarEvents(mostSimilarEventsIds.get(i), interactedEventIds.stream().toList())
                            .stream()
                            .collect(Collectors.toMap(
                                    RecommendedEvent::getId,
                                    RecommendedEvent::getScore
                            ));

                    Map<Long, Double> eventsAndRatings = feignEventController
                            .findEventsByIds(eventsAndSimilarities.keySet())
                            .stream()
                            .collect(Collectors.toMap(
                                    EventFullDto::getId,
                                    EventFullDto::getRating
                            ));

                    double weightedMarksSum = 0.0;
                    for (Long eventId : eventsAndSimilarities.keySet()) {
                        weightedMarksSum += eventsAndSimilarities.get(eventId) * eventsAndRatings.get(eventId);
                    }

                    double sumSimilarities = eventsAndSimilarities
                            .values()
                            .stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();

                    double result = weightedMarksSum / sumSimilarities;

                    RecommendationMessage.RecommendedEventProto event = RecommendationMessage.RecommendedEventProto.newBuilder()
                            .setEventId(mostSimilarEventsIds.get(i))
                            .setScore(result)
                            .build();
                    responseObserver.onNext(event);

                }
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

        List<Long> eventsByUserId = userActionRepository.findEventIdsByUserId(request.getUserId());
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
