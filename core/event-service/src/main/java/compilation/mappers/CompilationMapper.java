package compilation.mappers;

import compilation.model.Compilation;
import interaction.dto.compilation.CompilationDto;
import interaction.dto.compilation.NewCompilationDto;
import interaction.dto.compilation.UpdateCompilationRequest;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Objects;

public interface CompilationMapper {
    CompilationDto toCompilationDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "compilationDto.title")
    @Mapping(target = "pinned", source = "compilationDto.pinned")
    @Mapping(target = "events", source = "events")
    Compilation toUpdateCompilation(
            @MappingTarget Compilation compilation,
            UpdateCompilationRequest compilationDto,
            List<Objects> events
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation toCompilation(NewCompilationDto compilationDto, List<Objects> events);
}
