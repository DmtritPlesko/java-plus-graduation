package analyzer.repozitory;

import analyzer.model.UserAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    @Query("SELECT COUNT(DISTINCT a.userId) FROM UserAction a " +
            "WHERE a.action = :action AND a.eventId = :eventId " +
            "AND a.userId NOT IN (" +
            "   SELECT a2.userId FROM UserAction a2 " +
            "   WHERE a2.eventId = :eventId AND a2.action IN :excludedActions" +
            ")")
    long countUserIdsWithSpecificActionOnly(
            @Param("eventId") Long eventId,
            @Param("action") String action,
            @Param("excludedActions") List<String> excludedActions
    );

    Set<Long> findDistinctEventIdByUserId(Long uid);

    Page<Long> findEventIdByUserIdOrderByTimestamp(Long userId, Pageable pageable);

    List<Long> findDistinctEventIdByUserIdNot(Long uid);
}
