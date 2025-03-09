package compilation.service;

import org.springframework.data.domain.Pageable;
import interaction.dto.compilation.CompilationDto;
import interaction.dto.compilation.NewCompilationDto;
import interaction.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, Pageable pageRequest);

    CompilationDto getBy(Long id);

    CompilationDto add(NewCompilationDto compilationDto);

    void deleteBy(long id);

    CompilationDto updateBy(long id, UpdateCompilationRequest compilationDto);
}
