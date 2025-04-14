package analyzer.service;

import io.grpc.stub.StreamObserver;
import ru.practicum.grpc.stats.recommendation.RecommendationMessage;
import ru.practicum.grpc.stats.recommendation.RecommendationsControllerGrpc;

public class AnalyzerServerImpl extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    @Override
    public void getRecommendationsForUser(RecommendationMessage.UserPredictionsRequestProto request,
                                          StreamObserver<RecommendationMessage.RecommendedEventProto> responseObserver) {
        super.getRecommendationsForUser(request, responseObserver);
    }

    @Override
    public void getSimilarEvents(RecommendationMessage.SimilarEventsRequestProto request,
                                 StreamObserver<RecommendationMessage.RecommendedEventProto> responseObserver) {
        super.getSimilarEvents(request, responseObserver);
    }

    @Override
    public void getInteractionsCount(RecommendationMessage.InteractionsCountRequestProto request,
                                     StreamObserver<RecommendationMessage.RecommendedEventProto> responseObserver) {
        super.getInteractionsCount(request, responseObserver);
    }
}
