package ewm.clients;

import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.action.CollectorControllerGrpc;
import ru.practicum.grpc.stats.action.UserActionMessage;

import java.time.Instant;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CollectorClient {

    final CollectorControllerGrpc.CollectorControllerBlockingStub blockingStub;

    public CollectorClient(@GrpcClient("collector") CollectorControllerGrpc.CollectorControllerBlockingStub blockingStub) {
        this.blockingStub = blockingStub;

    }

    public void sendUserAction(long userId, long eventId, UserActionMessage.ActionTypeProto actionType) {
        UserActionMessage.UserActionProto userAction = UserActionMessage.UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();

        try {
            blockingStub.collectAction(userAction);
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

}
