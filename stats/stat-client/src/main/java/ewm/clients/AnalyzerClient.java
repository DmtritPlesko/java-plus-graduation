package ewm.clients;

import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.recommendation.RecommendationMessage;
import ru.practicum.grpc.stats.recommendation.RecommendationsControllerGrpc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnalyzerClient {
    private final RecommendationsControllerGrpc.RecommendationsControllerBlockingStub blockingStub;

    public AnalyzerClient(@GrpcClient("analyzer") RecommendationsControllerGrpc
            .RecommendationsControllerBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    public Set<Long> getSimilarEvents(long eventId, long userId, int maxResults) {
        RecommendationMessage.SimilarEventsRequestProto similarRequest = RecommendationMessage
                .SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        Set<Long> eventIdsToReturn = new HashSet<>();
        try {
            Iterator<RecommendationMessage.RecommendedEventProto> responseIterator = blockingStub
                    .getSimilarEvents(similarRequest);

            while (responseIterator.hasNext()) {
                RecommendationMessage.RecommendedEventProto event = responseIterator.next();
                eventIdsToReturn.add(event.getEventId());
            }
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
        return eventIdsToReturn;
    }

    public Set<Long> getRecommendationsForUser(long userId, int maxResults) {
        RecommendationMessage.UserPredictionsRequestProto userRequest = RecommendationMessage
                .UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        Set<Long> eventIdsToReturn = new HashSet<>();

        try {
            Iterator<RecommendationMessage.RecommendedEventProto> responseIterator = blockingStub
                    .getRecommendationsForUser(userRequest);

            while (responseIterator.hasNext()) {
                RecommendationMessage.RecommendedEventProto event = responseIterator.next();
                eventIdsToReturn.add(event.getEventId());
            }
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
        return eventIdsToReturn;
    }

    public Set<Long> getInteractionsCount(Iterable<Long> eventIds) {
        RecommendationMessage.InteractionsCountRequestProto interactionsRequest = RecommendationMessage
                .InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        Set<Long> eventIdsToReturn = new HashSet<>();

        try {
            Iterator<RecommendationMessage.RecommendedEventProto> responseIterator = blockingStub
                    .getInteractionsCount(interactionsRequest);

            while (responseIterator.hasNext()) {
                RecommendationMessage.RecommendedEventProto event = responseIterator.next();
                eventIdsToReturn.add(event.getEventId());
            }
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
        return eventIdsToReturn;
    }
}
