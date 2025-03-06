package interaction.dto.event;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import interaction.validation.EventDateInOneHour;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest extends UpdateEventRequest {
    AdminStateAction stateAction;
    @EventDateInOneHour
    LocalDateTime eventDate;
}
