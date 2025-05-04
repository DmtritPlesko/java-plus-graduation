package analyzer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendedEvent {

    Long id;
    Double score;

    public RecommendedEvent(Long eventId, double maxResult) {
        this.id = eventId;
        this.score = maxResult;
    }

}
