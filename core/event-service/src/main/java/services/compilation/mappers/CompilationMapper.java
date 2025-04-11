package services.compilation.mappers;

import interaction.dto.compilation.CompilationDto;
import interaction.dto.compilation.NewCompilationDto;
import interaction.dto.compilation.UpdateCompilationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import services.compilation.model.Compilation;
import services.event.mappers.EventMapper;
import services.event.mappers.UserMapper;
import services.event.model.Event;

import java.util.List;


@Mapper(componentModel = "spring", uses = {EventMapper.class, UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    CompilationDto toCompilationDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "compilationDto.title")
    @Mapping(target = "pinned", source = "compilationDto.pinned")
    @Mapping(target = "events", source = "events")
    Compilation toUpdateCompilation(
            @MappingTarget Compilation compilation,
            UpdateCompilationRequest compilationDto,
            List<Event> events
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation toCompilation(NewCompilationDto compilationDto, List<Event> events);

}
