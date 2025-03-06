package ru.practicum.subscription.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import interaction.controller.FeignUserController;
import ru.practicum.subscription.mapper.SubscriptionMapper;
import ru.practicum.subscription.mapper.UserMapper;
import ru.practicum.subscription.model.QSubscription;
import ru.practicum.subscription.model.Subscription;
import ru.practicum.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import interaction.dto.subscription.SubscriptionDto;
import interaction.dto.user.UserShortDto;
import interaction.exception.ConflictException;
import interaction.exception.ValidationException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    final JPAQueryFactory jpaQueryFactory;
    final FeignUserController feignUserController;
    final UserMapper userMapper;
    final QSubscription qSubscription;
    final SubscriptionRepository subRepository;
    final SubscriptionMapper subMapper;

    @Override
    public Set<UserShortDto> getFollowing(long userId, Pageable page) {

        return jpaQueryFactory
                .selectFrom(qSubscription)
                .leftJoin(qSubscription.following)
                .leftJoin(qSubscription.follower)
                .where(qSubscription.follower.id.eq(userId))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .stream()
                .map(Subscription::getFollowing)
                .map(userMapper::toUserShortDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UserShortDto> getFollowers(long userId, Pageable page) {
        return jpaQueryFactory
                .selectFrom(qSubscription)
                .leftJoin(qSubscription.following)
                .leftJoin(qSubscription.follower)
                .where(qSubscription.following.id.eq(userId))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .stream()
                .map(Subscription::getFollower)
                .map(userMapper::toUserShortDto)
                .collect(Collectors.toSet());
    }

    @Override
    public SubscriptionDto addFollow(long userId, long followingId) {
        if (userId == followingId) {
            throw new ConflictException("Невозможно подписаться на себя");
        }
        return subMapper
                .toSubscriptionDto(subRepository
                        .save(subMapper.toSubscription(
                                new Subscription(),
                                feignUserController.getBy(userId),
                                feignUserController.getBy(followingId)
                        )));
    }

    @Override
    public void deleteFollow(long userId, long followingId) {

        Optional<Subscription> subscription = subRepository.findByFollowingId(followingId);

        if (subscription.isEmpty()) {
            throw new ValidationException("Нет такого подписчика");
        }

        subRepository.deleteById(subscription.get().getId());

    }
}
