package analyzer.repozitory;

import analyzer.model.EventSimilarity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
}
