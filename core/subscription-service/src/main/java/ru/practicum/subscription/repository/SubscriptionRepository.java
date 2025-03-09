package ru.practicum.subscription.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.subscription.model.Subscription;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByFollowingId(long followingId, Pageable pageable);

    Optional<Subscription> findByFollowerId(long followerId,Pageable pageable );

    Optional<Subscription> findByFollowingId(long followingId);

}
