package ru.practicum.subscription.mapper;

import interaction.dto.subscription.SubscriptionDto;
import interaction.dto.user.UserDto;
import org.springframework.stereotype.Component;
import ru.practicum.subscription.model.Subscription;


@Component
public class SubscriptionMapperImpl implements SubscriptionMapper {
    @Override
    public SubscriptionDto toSubscriptionDto(Subscription subscription) {

        SubscriptionDto subscriptionDto = new SubscriptionDto();

        subscriptionDto.setId(subscription.getId());

        subscriptionDto.getFollower().setId(subscription.getFollowerId());

        subscriptionDto.getFollowing().setId(subscription.getFollowingId());

        subscriptionDto.setCreated(subscription.getCreated());

        return subscriptionDto;
    }

    @Override
    public Subscription toSubscription(Subscription subscription, UserDto follower, UserDto following) {

        subscription.setFollowerId(follower.getId());
        subscription.setFollowingId(following.getId());

        return subscription;

    }
}
