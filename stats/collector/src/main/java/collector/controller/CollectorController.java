package collector.controller;

import collector.mapper.CollectorActionMapper;
import collector.service.CollectorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.action.CollectorControllerGrpc;
import ru.practicum.grpc.stats.action.UserActionMessage;

import java.time.Instant;

@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    final CollectorService collectorService;

    public void CollectAction(UserActionMessage.UserActionProto userActionProto) {

        collectorService.sendAction(new UserActionAvro(userActionProto.getUserId(),
                userActionProto.getEventId(),
                CollectorActionMapper.toActionTypeAvro(userActionProto),
                Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(),
                        userActionProto.getTimestamp().getNanos())
        ));
    }
}
