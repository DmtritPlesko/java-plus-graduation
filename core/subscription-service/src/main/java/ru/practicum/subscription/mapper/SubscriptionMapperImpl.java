package ru.practicum.subscription.mapper;

import ru.practicum.subscription.model.Subscription;
import org.springframework.stereotype.Component;
import interaction.dto.subscription.SubscriptionDto;
import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import ru.practicum.subscription.model.User;

@Component
public class SubscriptionMapperImpl implements SubscriptionMapper{
    @Override
    public SubscriptionDto toSubscriptionDto(Subscription subscription) {

        SubscriptionDto subscriptionDto = new SubscriptionDto();

        subscriptionDto.setId(subscription.getId());

        subscriptionDto.setFollower(new UserShortDto(subscription.getFollower().getId(),
                subscription.getFollower().getName()));

        subscriptionDto.setFollowing( new UserShortDto(subscription.getFollowing().getId(),
                subscription.getFollowing().getName()));

        subscriptionDto.setCreated(subscription.getCreated());

        return subscriptionDto;
    }

    @Override
    public Subscription toSubscription(Subscription subscription, UserDto follower, UserDto following) {

        subscription.setFollower(new User(follower.getId(), follower.getName()));
        subscription.setFollowing( new User(following.getId(),following.getName()));

        return subscription;

    }
}
