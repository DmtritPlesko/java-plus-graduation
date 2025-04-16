package analyzer.repozitory;

import analyzer.model.EventSimilarity;
import analyzer.model.RecommendedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    @Query("SELECT e FROM EventSimilarity e " +
            "WHERE (e.eventIdA = :eventId OR e.eventIdB = :eventId) " +
            "AND NOT (e.eventIdA IN :interactedList " +
            "AND e.eventIdA IN :interactedList) " +
            "ORDER BY e.result DESC")
    Page<EventSimilarity> findSimilaritiesExcludingInteracted(
            @Param("eventId") Long eventId,
            @Param("interactedList") List<Long> interactedList,
            Pageable pageable
    );

    @Query(value = "SELECT CASE " +
            "WHEN e.event_id_a IN :notInteractedList AND e.event_id_b IN :interactedList THEN e.event_id_a " +
            "WHEN e.event_id_b IN :notInteractedList AND e.event_id_b IN :interactedList THEN e.event_id_b " +
            "END AS event_id " +
            "FROM EventSimilarity e " +
            "WHERE (e.event_id_a IN :notInteractedList AND e.event_id_b IN :interactedList) " +
            "   OR (e.event_id_b IN :notInteractedList AND e.event_id_a IN :interactedList) " +
            "ORDER BY s.score DESC",
            nativeQuery = true)
    List<Long> findMostSimilarEventsIds(
            @Param("interactedList") List<Long> interactedList,
            @Param("notInteractedList") List<Long> notInteractedList,
            Pageable pageable
    );

    @Query("SELECT NEW analyzer.model.RecommendedEvent(" +
            "CASE WHEN e.eventIdA = :eventId THEN e.eventIdB ELSE s.eventIdA END, " +
            "e.result) " +
            "FROM EventSimilarity e " +
            "WHERE (e.eventIdA = :eventId OR e.eventIdB = :eventId) " +
            "AND (e.eventIdA IN :interactedList OR s.eventIdB IN :interactedList) " +
            "ORDER BY s.score DESC")
    List<RecommendedEvent> findSimilarEvents(
            @Param("eventId") Long eventId,
            @Param("interactedList") List<Long> interactedList
    );

    @Query("SELECT s FROM Similarity s WHERE s.eventId IN :eventIds")
    Map<Long, Map<Long, Double>> batchFindSimilarEvents(
            @Param("eventIds") List<Long> eventIds,
            @Param("interactedIds") List<Long> interactedIds);

}
