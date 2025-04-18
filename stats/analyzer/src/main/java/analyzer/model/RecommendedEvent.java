package analyzer.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendedEvent {

    Long id;
    Long score;

}
