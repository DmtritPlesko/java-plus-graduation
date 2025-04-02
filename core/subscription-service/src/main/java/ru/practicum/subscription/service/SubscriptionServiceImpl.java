package ru.practicum.subscription.service;

import feign.FeignException;
import interaction.controller.FeignUserController;
import interaction.dto.subscription.SubscriptionDto;
import interaction.dto.user.UserDto;
import interaction.dto.user.UserShortDto;
import interaction.exception.ConflictException;
import interaction.exception.NotFoundException;
import interaction.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.subscription.mapper.SubscriptionMapper;
import ru.practicum.subscription.mapper.UserMapper;
import ru.practicum.subscription.model.Subscription;
import ru.practicum.subscription.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    final FeignUserController feignUserController;
    final UserMapper userMapper;
    final SubscriptionRepository subRepository;
    final SubscriptionMapper subMapper;

    @Override
    public Set<UserShortDto> getFollowing(long userId, Pageable page) {

        List<Long> longList = subRepository.findByFollowerId(userId, page).stream()
                .map(Subscription::getFollowingId)
                .toList();

        return feignUserController.getAllBy(longList, 0, page.getPageSize())
                .stream()
                .map(userMapper::userDtoToUserShortDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<UserShortDto> getFollowers(long userId, Pageable page) {

        List<Long> longList = subRepository.findByFollowingId(userId, page).stream()
                .map(Subscription::getFollowerId)
                .toList();

        return feignUserController.getAllBy(longList, 0, page.getPageSize())
                .stream()
                .map(userMapper::userDtoToUserShortDto)
                .collect(Collectors.toSet());
    }

    @Override
    public SubscriptionDto addFollow(long userId, long followingId) {

        if (userId == followingId) {
            throw new ConflictException("Невозможно подписаться на себя");
        }

        UserDto follower;
        try {
            follower = feignUserController.getBy(userId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Пользователь который хочет подписаться с ID " + userId + " не найден");
        }

        UserDto following;
        try {
            following = feignUserController.getBy(followingId);
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Пользователь на которого хотят подписаться с ID " + followingId + " не найден");
        }

        SubscriptionDto subscriptionDto = subMapper.toSubscriptionDto(subRepository.save(subMapper.toSubscription(
                new Subscription(),
                follower.getId(),
                following.getId()
        )));

        subscriptionDto.getFollower().setName(follower.getName());
        subscriptionDto.getFollowing().setName(following.getName());
        subscriptionDto.setCreated(LocalDateTime.now());
        return subscriptionDto;
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
