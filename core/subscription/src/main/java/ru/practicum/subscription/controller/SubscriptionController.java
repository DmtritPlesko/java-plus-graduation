package ru.practicum.subscription.controller;

import interaction.dto.subscription.SubscriptionDto;
import interaction.dto.user.UserShortDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.subscription.service.SubscriptionService;

import java.util.Set;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SubscriptionController {
    final SubscriptionService subscriptionService;

    @GetMapping("/users/{userId}/subscriptions/following")
    Set<UserShortDto> getFollowingBy(@PathVariable("userId") long userId,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {

        return subscriptionService.getFollowing(userId, PageRequest.of(from, size));

    }

    @GetMapping("/users/{userId}/subscriptions/followers")
    Set<UserShortDto> getFollowersBy(@PathVariable("userId") long userId,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {

        return subscriptionService.getFollowers(userId, PageRequest.of(from, size));

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/subscriptions/{followingId}")
    SubscriptionDto addFollow(@PathVariable("userId") long userId,
                              @PathVariable("followingId") long followingId) {

        return subscriptionService.addFollow(userId, followingId);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}/subscriptions/{followingId}")
    void deleteFollow(@PathVariable("userId") long userId,
                      @PathVariable("followingId") long followingId) {

        subscriptionService.deleteFollow(userId, followingId);

    }
}
