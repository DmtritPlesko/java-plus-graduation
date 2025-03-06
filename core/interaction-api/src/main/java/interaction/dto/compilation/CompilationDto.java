package interaction.dto.compilation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import interaction.dto.event.EventShortDto;

import java.util.Set;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;
    boolean pinned;
    String title;
    Set<EventShortDto> events;
}
