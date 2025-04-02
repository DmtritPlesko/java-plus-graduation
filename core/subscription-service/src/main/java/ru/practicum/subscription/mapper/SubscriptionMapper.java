package ru.practicum.subscription.mapper;


import interaction.dto.subscription.SubscriptionDto;
import ru.practicum.subscription.model.Subscription;

public interface SubscriptionMapper {
    SubscriptionDto toSubscriptionDto(Subscription subscription);

    Subscription toSubscription(Subscription subscription, Long follower, Long following);
}
