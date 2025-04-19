package collector.mapper;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.grpc.stats.action.UserActionMessage;

public class CollectorActionMapper {

    public static ActionTypeAvro toActionTypeAvro(UserActionMessage.UserActionProto userActionProto) {
        return switch (userActionProto.getActionType()) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> null;
        };
    }

}
