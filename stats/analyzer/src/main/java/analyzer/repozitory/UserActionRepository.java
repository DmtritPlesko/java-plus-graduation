package analyzer.repozitory;

import analyzer.model.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
}
