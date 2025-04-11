package interaction.dto.compilation;

import interaction.dto.event.EventShortDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

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
