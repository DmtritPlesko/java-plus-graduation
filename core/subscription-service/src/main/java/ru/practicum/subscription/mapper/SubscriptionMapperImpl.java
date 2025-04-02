package ru.practicum.subscription.mapper;

import interaction.dto.subscription.SubscriptionDto;
import interaction.dto.user.UserShortDto;
import org.springframework.stereotype.Component;
import ru.practicum.subscription.model.Subscription;


@Component
public class SubscriptionMapperImpl implements SubscriptionMapper {
    @Override
    public SubscriptionDto toSubscriptionDto(Subscription subscription) {

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setId(subscription.getId());
        subscriptionDto.setFollower(new UserShortDto(subscription.getFollowerId()));
        subscriptionDto.setFollowing(new UserShortDto(subscription.getFollowingId()));
        subscriptionDto.setCreated(subscription.getCreated());

        return subscriptionDto;
    }

    @Override
    public Subscription toSubscription(Subscription subscription, Long follower, Long following) {

        subscription.setFollowerId(follower);
        subscription.setFollowingId(following);

        return subscription;

    }
}
