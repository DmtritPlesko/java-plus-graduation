package ewm.subscription.service;
import org.springframework.data.domain.Pageable;

import ewm.subscription.dto.SubscriptionDto;
import ewm.user.dto.UserShortDto;
import java.util.Set;

public interface SubscriptionService {
    Set<UserShortDto> getFollowing(long userId, Pageable page);

    Set<UserShortDto> getFollowers(long userId, Pageable page);

    SubscriptionDto addFollow(long userId, long followingId);

    void deleteFollow(long userId, long followingId);
}
