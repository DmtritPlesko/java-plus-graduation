package event.mappers;


import interaction.dto.event.AdminStateAction;
import interaction.dto.event.EventState;
import interaction.dto.event.UserStateAction;

public interface StateActionMapper {
    default EventState toEventState(UserStateAction userStateAction) {
        return switch (userStateAction) {
            case SEND_TO_REVIEW -> EventState.PENDING;
            case CANCEL_REVIEW -> EventState.CANCELED;
        };
    }

    default EventState toEventState(AdminStateAction adminStateAction) {
        return switch (adminStateAction) {
            case PUBLISH_EVENT -> EventState.PUBLISHED;
            case REJECT_EVENT -> EventState.CANCELED;
        };
    }
}
