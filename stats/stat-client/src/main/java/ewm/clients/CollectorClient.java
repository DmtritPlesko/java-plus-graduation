package ewm.clients;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.action.UserActionControllerGrpc;
import ru.practicum.grpc.stats.action.UserActionMessage;

import java.time.Instant;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectorClient {

    final UserActionControllerGrpc.UserActionControllerBlockingStub blockingStub;

    public CollectorClient(@GrpcClient("collector") UserActionControllerGrpc.UserActionControllerBlockingStub blockingStub) {
        this.blockingStub = blockingStub;

    }

    public void sendUserAction(long userId, long eventId, UserActionMessage.ActionTypeProto actionType) {
        UserActionMessage.UserActionRequest userAction = UserActionMessage.UserActionRequest.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();

        try {
            blockingStub.collectUserAction(userAction);
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

}
