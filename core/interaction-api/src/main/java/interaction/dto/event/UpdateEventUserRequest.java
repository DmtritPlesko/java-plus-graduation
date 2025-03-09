package interaction.dto.event;

import interaction.validation.EventDateInTwoHours;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest extends UpdateEventRequest {
    UserStateAction stateAction;
    @EventDateInTwoHours
    LocalDateTime eventDate;
}
