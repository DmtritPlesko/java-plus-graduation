package ewm.subscription.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import ewm.subscription.dto.SubscriptionDto;
import ewm.subscription.mapper.SubscriptionMapper;
import ewm.subscription.model.QSubscription;
import ewm.subscription.model.Subscription;
import ewm.subscription.repository.SubscriptionRepository;
import ewm.user.dto.UserShortDto;
import ewm.user.mappers.UserMapper;
import ewm.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    final JPAQueryFactory jpaQueryFactory;
    final UserService userService;
    final UserMapper userMapper;
    final QSubscription qSubscription = QSubscription.subscription;
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

        return subMapper.
                toSubscriptionDto(subRepository.
                        save(subMapper.toSubscription(
                                new Subscription(),
                                userService.getBy(userId),
                                userService.getBy(followingId)
                        )));
    }

    @Override
    public void deleteFollow(long userId, long followingId) {
        subRepository.deleteFollow(userId,followingId);
    }
}
