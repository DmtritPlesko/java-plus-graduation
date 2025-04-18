package collector.controller;

import collector.mapper.CollectorActionMapper;
import collector.service.CollectorService;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.action.UserActionControllerGrpc;
import ru.practicum.grpc.stats.action.UserActionMessage;

import java.time.Instant;

@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectorController extends UserActionControllerGrpc.UserActionControllerImplBase {

    final CollectorService collectorService;

    @Override
    public void collectUserAction(UserActionMessage.UserActionRequest request,
                                  StreamObserver<UserActionMessage.UserActionResponse> responseObserver) {
        collectorService.sendAction(new UserActionAvro(request.getUserId(),
                request.getEventId(),
                CollectorActionMapper.toActionTypeAvro(request),
                Instant.ofEpochSecond(request.getTimestamp().getSeconds(),
                        request.getTimestamp().getNanos())
        ));
        UserActionMessage.UserActionResponse response = UserActionMessage.UserActionResponse.newBuilder()
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
