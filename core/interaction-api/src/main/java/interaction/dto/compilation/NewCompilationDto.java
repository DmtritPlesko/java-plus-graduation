package interaction.dto.compilation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class NewCompilationDto {
    boolean pinned;

    @NotNull
    @Size(min = 1, max = 50)
    String title;

    Set<Long> events;
}
