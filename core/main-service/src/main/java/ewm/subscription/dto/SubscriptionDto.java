package ewm.subscription.dto;

import ewm.user.dto.UserShortDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionDto {

    Long id;
    UserShortDto follower;
    UserShortDto following;
    LocalDateTime created;
}
