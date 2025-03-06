package ru.practicum.subscription.service;

import org.springframework.data.domain.Pageable;
import interaction.dto.subscription.SubscriptionDto;
import interaction.dto.user.UserShortDto;

import java.util.Set;

public interface SubscriptionService {
    Set<UserShortDto> getFollowing(long userId, Pageable page);

    Set<UserShortDto> getFollowers(long userId, Pageable page);

    SubscriptionDto addFollow(long userId, long followingId);

    void deleteFollow(long userId, long followingId);
}
