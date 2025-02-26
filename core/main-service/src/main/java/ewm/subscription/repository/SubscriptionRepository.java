package ewm.subscription.repository;

import ewm.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Modifying
    @Query("DELETE FROM Subscription s WHERE s.followerId = :followerId AND s.followingId = :followingId")
    void deleteFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

}
